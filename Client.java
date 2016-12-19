import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client {

	// 客户端界面
	public static JTextArea jta, jtb;
	public static Window myClient;
	public static PipedInputStream pin;
	public static PipedOutputStream pout;

	public static class Window {
		Point position;
		JFrame jf;
		JPanel jp;
		JButton jb1, jb2;
		JScrollPane jsp1, jsp2;

		public Window() {
			position = new Point();
			jf = new JFrame("拍卖行客户端");
			jf.setDefaultCloseOperation(3);

			Container contentPane = jf.getContentPane();
			contentPane.setLayout(new BorderLayout(20, 20));

			// 设置信息框
			jta = new JTextArea(10, 15);
			jta.setTabSize(4);
			jta.setFont(new Font("微软雅黑", Font.PLAIN, 25));
			jta.setLineWrap(true);
			jta.setForeground(Color.white);
			jta.setBackground(Color.black);
			jta.setWrapStyleWord(true);
			jta.setEditable(false);

			jtb = new JTextArea(1, 15);
			jtb.setTabSize(4);
			jtb.setFont(new Font("微软雅黑", Font.PLAIN, 25));
			jtb.setLineWrap(true);
			jtb.setForeground(Color.black);
			jtb.setBackground(Color.white);
			jtb.setWrapStyleWord(true);

			((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
			jsp1 = new JScrollPane(jta);
			contentPane.add(jsp1, BorderLayout.NORTH);

			jsp2 = new JScrollPane(jtb);
			contentPane.add(jsp2, BorderLayout.CENTER);

			jp = new JPanel();
			jp.setBorder(BorderFactory.createEmptyBorder(0,50,0,50));
			jp.setLayout(new GridLayout(1, 2, 50, 50));
			jb1 = new JButton("发送指令");
			jp.add(jb1);
			jb2 = new JButton("尝试退出");
			jp.add(jb2);
			contentPane.add(jp, BorderLayout.SOUTH);

			jf.setUndecorated(true);
			jf.setOpacity(0.8f);
			jf.setSize(800, 500);
			jf.setLocation(600, 400);
			jf.setVisible(true);

			jf.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					position.x = e.getX();
					position.y = e.getY();
				}
			});
			jf.addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					Point p = jf.getLocation();
					jf.setLocation(p.x + e.getX() - position.x, p.y + e.getY() - position.y);
				}
			});

			jb1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String s = jtb.getText();
					try {
						pout.write(s.getBytes());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					jtb.setText("");
				}
			});

			jb2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (bidderID == -1 && masterID == -1) {
						System.exit(0);
					}
					else {
						String s = "quit";
						try {
							pout.write(s.getBytes());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						jtb.setText("");
					}
				}
			});
		}
	}

	// 指令处理及发送
	private static List<Reciver> fool = new ArrayList<Reciver>();

	public static DatagramSocket socket;
	public static boolean mainFlag, reciverFlag;
	public static int bidderID;
	public static int masterID;
	public static String bidderName;
	public static int auctionNo;
	public static String[] command;

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}
		return true;
	}

	public static int getOrdernum(String str) {
		for (int i = 0; i <= 14; i++) {
			if (str.equals(command[i]))
				return i;
		}
		return 20;
	}

	public static void main(String[] args) throws IOException {
		pin = new PipedInputStream();
		pout = new PipedOutputStream();
		pin.connect(pout);

		InetAddress address = InetAddress.getByName("localhost");
		InetAddress myaddress = address;
		socket = new DatagramSocket();
		int port = 2333;
		int myport = socket.getLocalPort();

		String info;
		byte[] data2;
		DatagramPacket packet2;

		mainFlag = true;
		reciverFlag = true;
		bidderID = -1;
		masterID = -1;
		auctionNo = -1;

		command = new String[20];
		command[0] = "register";
		command[1] = "login";
		command[2] = "auctions";
		command[3] = "join";
		command[4] = "list";
		command[5] = "bid";
		command[6] = "leave";
		command[7] = "master";
		command[8] = "msg";
		command[9] = "enter";
		command[10] = "kickout";
		command[11] = "open";
		command[12] = "close";
		command[13] = "quit";
		command[14] = "error";

		new Client().startReciver();
		myClient = new Window();

		// 登陆
		jta.append("****客户端已经启动*****" + "\r\n");
		jta.append("请登录或注册" + "\r\n");

		while (true) {
			byte[] buf = new byte[1024];
			int len = pin.read(buf);
			info = new String(buf, 0, len);
			String[] logstr = new String[] {};
			logstr = info.split(" ");
			int ordernum = getOrdernum(logstr[0]);
			if (ordernum == 0 || ordernum == 1 || ordernum == 7) {
				if (logstr.length == 3) {
					info = ordernum + " " + logstr[1] + " " + logstr[2];
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (bidderID != -1 || masterID != -1)
						break;
				} else {
					jta.append("指令有误，请重新检查" + "\r\n");

				}
			} else {
				jta.append("指令无效，请先登陆或注册" + "\r\n");

			}
		}

		// 发送指令
		while (true) {
			byte[] buf = new byte[1024];
			int len = pin.read(buf);
			info = new String(buf, 0, len);
			String[] order = new String[] {};
			order = info.split(" ");
			int ordernum = getOrdernum(order[0]);

			int orderFlag = 0;

			if (ordernum == 2 && order.length == 1) {
				jta.append("正在获取竞拍室列表，请稍等" + "\r\n");

				data2 = "2 ".getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				socket.send(packet2);
			}

			else if (ordernum == 3 && order.length == 2) {
				if (auctionNo == -1) {
					if (masterID == -1 && isNumeric(order[1])) {
						jta.append("正在加入竞拍室，请稍等" + "\r\n");

						info = "3 " + order[1] + " " + bidderID;
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else
						orderFlag = 1;
				} else {
					jta.append("抱歉，你已进入竞拍室" + "\r\n");

				}
			}

			else if (ordernum == 4 && order.length == 1) {
				if (auctionNo != -1) {
					jta.append("正在获取竞拍者名单，请稍等" + "\r\n");

					info = "4 " + auctionNo;
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
				} else {
					jta.append("抱歉，你尚未进入竞拍室" + "\r\n");

				}
			}

			else if (ordernum == 5 && order.length == 2) {
				if (auctionNo != -1) {
					if (masterID == -1 && isNumeric(order[1])) {
						jta.append("正在竞价，请稍等" + "\r\n");

						info = "5 " + order[1] + " " + auctionNo + " " + bidderID;
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else
						orderFlag = 1;
				} else {
					jta.append("抱歉，你尚未进入竞拍室" + "\r\n");

				}
			}

			else if (ordernum == 6 && order.length == 1) {
				if (auctionNo != -1) {
					if (masterID == -1) {
						jta.append("正在尝试离开，请稍等" + "\r\n");

						info = "6 " + auctionNo + " " + bidderID;
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else {
						jta.append("正在离开，请稍等" + "\r\n");
						jta.append("离开成功" + "\r\n");
						info = "6 " + auctionNo;
						auctionNo = -1;
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					}
				} else {
					jta.append("抱歉，你尚未进入竞拍室" + "\r\n");

				}
			}

			else if (ordernum == 8) {
				if (masterID != -1) {
					if (order.length == 2) {
						jta.append("正在群发信息，请稍等" + "\r\n");

						info = "8 " + order[1];
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else if (order.length == 3 && isNumeric(order[1])) {
						jta.append("正在发送信息，请稍等" + "\r\n");

						info = "8 " + order[1] + " " + order[2];
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					}
				} else
					orderFlag = 1;
			}

			else if (ordernum == 9 && order.length == 2) {
				if (masterID != -1 && isNumeric(order[1])) {
					jta.append("正在进入竞拍室，请稍等" + "\r\n");

					info = "9 " + order[1];
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
				} else
					orderFlag = 1;
			}

			else if (ordernum == 10) {
				if (auctionNo != -1) {
					if (order.length == 2 && masterID != -1 && isNumeric(order[1])) {
						jta.append("正在踢出用户，请稍等" + "\r\n");

						info = "10 " + order[1] + " " + auctionNo;
						data2 = info.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else
						orderFlag = 1;
				} else {
					jta.append("抱歉，你尚未进入竞拍室" + "\r\n");

				}
			}

			else if (ordernum == 11 && order.length == 3) {
				if (masterID != -1 && isNumeric(order[2])) {
					jta.append("正在创建新的竞拍室，请稍等" + "\r\n");

					info = "11 " + order[1] + " " + order[2];
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
				} else
					orderFlag = 1;
			}

			else if (ordernum == 12 && order.length == 2) {
				if (masterID != -1 && isNumeric(order[1])) {
					jta.append("正在关闭竞拍室，请稍等" + "\r\n");

					info = "12 " + order[1];
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
				} else
					orderFlag = 1;
			}

			else if (ordernum == 13 && order.length == 1) {

				if (bidderID != -1 && auctionNo != -1) {
					info = "13 " + auctionNo + " " + bidderID;
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
				} else {
					info = "13, ";
					data2 = info.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, myaddress, myport);
					socket.send(packet2);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!reciverFlag)
					break;
			}

			else
				orderFlag = 1;

			if (orderFlag == 1) {
				jta.append("指令有误或权限不足，请重新输入" + "\r\n");

			}
		}

		mainFlag = false;

		// 关闭客户端
		socket.close();
		jta.append("*****客户端关闭*********" + "\r\n");
		jta.append("3秒后退出..." + "\r\n");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void startReciver() throws IOException {
		Reciver channel = new Reciver();
		fool.add(channel);
		new Thread(channel).start();
	}

	// 接收指令
	private class Reciver implements Runnable {
		private byte[] data2;
		private DatagramPacket packet2;
		private String reply;

		public Reciver() throws IOException {
			data2 = new byte[1024];
			packet2 = new DatagramPacket(data2, data2.length);
		}

		public void run() {
			while (mainFlag) {
				try {
					socket.receive(packet2);
				} catch (IOException e) {
					e.printStackTrace();
				}
				reply = new String(data2, 0, packet2.getLength());
				String[] info = new String[] {};
				info = reply.split(", ");
				int infornum = Integer.parseInt(info[0]);

				// login
				if (infornum == 1) {
					int id = Integer.parseInt(info[2]);
					bidderID = id;
					jta.append(info[1] + "\r\n");
				}

				// master
				else if (infornum == 7) {
					int id = Integer.parseInt(info[2]);
					masterID = id;
					jta.append(info[1] + "\r\n");

				}

				// auctions
				else if (infornum == 2) {
					if (info.length % 3 == 1) {
						jta.append("竞拍室列表" + "\r\n");
						jta.append("*********************" + "\r\n");
						jta.append("序号         名字          价格" + "\r\n");
						for (int i = 1; i < info.length; i += 3)
							jta.append(info[i] + "     " + info[i + 1] + "     " + info[i + 2] + "\r\n");
						jta.append("*********************" + "\r\n");

					} else {
						jta.append("获取竞拍室列表成功，但传输过程中出错" + "\r\n");

					}
				}

				// join || enter
				else if (infornum == 3 || infornum == 9) {
					int no = Integer.parseInt(info[2]);
					auctionNo = no;
					jta.append(info[1] + "\r\n");

				}

				// list
				else if (infornum == 4) {
					jta.append("竞拍者名单" + "\r\n");
					jta.append("*********************" + "\r\n");
					for (int i = 1; i < info.length; i++)
						jta.append(info[i] + "\r\n");
					jta.append("*********************" + "\r\n");

				}

				// leave
				else if (infornum == 6) {
					auctionNo = -1;
					jta.append(info[1] + "\r\n");

				}

				// quit
				else if (infornum == 13) {
					auctionNo = -1;
					break;
				}

				else {
					jta.append(info[1] + "\r\n");

				}
			}
			reciverFlag = false;
		}
	}
}