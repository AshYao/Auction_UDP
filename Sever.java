import java.io.*;
import java.net.*;
import java.util.*;

/*
 * 服务器端，提供数据存储及响应功能
 */
public class Sever {
	// 拍卖倒计时线程Clock的List
	private static List<Clock> empress = new ArrayList<Clock>();

	public static DatagramSocket socket;
	public static InetAddress myaddress;
	public static int myport;

	public static class Auction {
		public int no;
		public String name;
		public int price;
		public boolean flag;
		public boolean countDown;
		public int bidderID;
		public String biddername;
		public int bidderNum;
		public List<Bidder> bidderList;

		public Auction(int no, String name, int price) {
			this.no = no;
			this.name = name;
			this.price = price;
			this.flag = true;
			this.countDown = false;
			this.bidderID = -1;
			this.biddername = null;
			this.bidderNum = 0;
			this.bidderList = new ArrayList<Bidder>();
		}
	}

	public static class Bidder {
		public int id;
		public String biddername;
		public String password;
		public boolean flag;
		public InetAddress address;
		public int port;

		public Bidder(int id, String biddername, String password) {
			this.id = id;
			this.biddername = biddername;
			this.password = password;
			this.flag = false;
		}

		public Bidder(int id, String biddername, String password, InetAddress address, int port) {
			this.id = id;
			this.biddername = biddername;
			this.flag = false;
			this.password = password;
			this.address = address;
			this.port = port;
		}
	}

	public static class Master {
		public int id;
		public String mastername;
		public String password;
		public boolean flag;
		public InetAddress address;
		public int port;

		public Master(int id, String mastername, String password) {
			this.id = id;
			this.mastername = mastername;
			this.password = password;
			this.flag = false;
		}

	}

	// 拍卖室Auction的List
	public static List<Auction> magican;

	public static void main(String[] args) throws IOException {

		magican = new ArrayList<Auction>();
		List<Bidder> emperor = new ArrayList<Bidder>();
		List<Master> hierophant = new ArrayList<Master>();

		// 与客户端相同的指令列表，无实际意义
		String[] command = new String[20];
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

		// 初始化商品
		int label = 0;
		Auction tmp = new Auction(label, "The Chariot", 7);
		magican.add(label, tmp);
		label++;

		tmp = new Auction(label, "Wheel of Fortune", 10);
		magican.add(label, tmp);
		label++;

		// 初始化用户
		int ulabel = 0;
		Bidder utmp = new Bidder(ulabel, "1", "1");
		emperor.add(ulabel, utmp);
		ulabel++;
		utmp = new Bidder(ulabel, "2", "2");
		emperor.add(ulabel, utmp);
		ulabel++;

		// 初始化管理员
		int mlabel = 0;
		Master mtmp = new Master(mlabel, "3", "3");
		hierophant.add(mlabel, mtmp);
		mlabel++;

		socket = new DatagramSocket(2333);
		myaddress = InetAddress.getByName("localhost");
		myport = 2333;
		InetAddress address;
		int port;
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		String info;
		String reply = null;
		byte[] data2;
		DatagramPacket packet2;
		
		Sever server  = new Sever();
		
		System.out.println("****服务器端已经启动****");

		while (true) {
			socket.receive(packet);
			info = new String(data, 0, packet.getLength());
			address = packet.getAddress();
			port = packet.getPort();

			// 获取指令
			String[] order = new String[] {};
			order = info.split(" ");
			int ordernum = Integer.parseInt(order[0]);

			switch (ordernum) {
			// register
			case 0: {
				boolean flag = true;
				if (!emperor.isEmpty()) {
					for (int i = 0; i < emperor.size(); i++) {
						if (emperor.get(i).biddername.equals(order[1])) {
							reply = "14, 用户名已被使用";
							flag = false;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, address, port);
							socket.send(packet2);
							break;
						}
					}
				}
				if (flag) {
					reply = "0, 注册成功";
					utmp = new Bidder(ulabel, order[1], order[2], address, port);
					emperor.add(ulabel, utmp);
					ulabel++;
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				}
				break;
			}

				// login
			case 1: {
				boolean flag = true;
				if (!emperor.isEmpty()) {
					for (int i = 0; i < emperor.size(); i++) {
						if (emperor.get(i).biddername.equals(order[1]) && emperor.get(i).password.equals(order[2])) {
							reply = "1, 登陆成功，尊敬的用户, " + emperor.get(i).id;
							flag = false;
							emperor.get(i).address = address;
							emperor.get(i).port = port;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, address, port);
							socket.send(packet2);
							break;
						}
					}
				}
				if (flag) {
					reply = "14, 用户名或密码错误";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				}
				break;
			}

			// master
			case 7: {
				boolean flag = true;
				if (!hierophant.isEmpty()) {
					for (int i = 0; i < hierophant.size(); i++) {
						if (hierophant.get(i).mastername.equals(order[1])
								&& hierophant.get(i).password.equals(order[2])) {
							flag = false;
							reply = "7, 登陆成功，管理员你好, " + hierophant.get(i).id;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, address, port);
							socket.send(packet2);
							break;
						}
					}
				}
				if (flag) {
					reply = "14, 用户名或密码错误";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				}
				break;
			}
			
			// auctions
			case 2: {
				reply = "2, ";
				if (!magican.isEmpty()) {
					for (int i = 0; i < magican.size(); i++) {
						if (magican.get(i).flag == true) {
							reply += magican.get(i).no + ", ";
							reply += magican.get(i).name + ", ";
							reply += magican.get(i).price + ", ";
						}
					}
				}
				data2 = reply.getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				socket.send(packet2);
				break;
			}
			
			// join
			case 3: {
				int no = Integer.parseInt(order[1]);
				int id = Integer.parseInt(order[2]);
				if(no < magican.size()){
					if (magican.get(no).flag == true){
						tmp = magican.get(no);
						utmp = emperor.get(id);
						if(tmp.bidderNum != 0){
							reply = "8, 用户" + utmp.biddername + "加入拍卖室";
							data2 = reply.getBytes();
							for (int i = 0; i < tmp.bidderList.size(); i++) {
								packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
										tmp.bidderList.get(i).port);
								socket.send(packet2);
							}
						}
						tmp.bidderList.add(tmp.bidderNum, utmp);
						tmp.bidderNum++;
						reply = "3, 加入竞拍室成功, " + no;
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					}
				}
					reply = "14, 查无此竞拍室或该竞拍已结束";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
			}

			// list
			case 4: {
				reply = "4, ";
				int no = Integer.parseInt(order[1]);
				for (int i = 0; i < magican.get(no).bidderList.size(); i++) {
					utmp = magican.get(no).bidderList.get(i);
					reply += utmp.biddername + ", ";
				}
				data2 = reply.getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				socket.send(packet2);
				break;
			}

			// bid
			case 5: {
				int price = Integer.parseInt(order[1]);
				int no = Integer.parseInt(order[2]);
				int id = Integer.parseInt(order[3]);
				if (magican.get(no).flag) {
					tmp = magican.get(no);
					if (tmp.bidderID ==- 1 && price > tmp.price) {
						utmp = emperor.get(id);
						tmp.price = price;
						tmp.bidderID = id;
						tmp.biddername = utmp.biddername;
						tmp.countDown = true;
						server.startClock(tmp);
						reply = "14, 用户" + tmp.biddername + "为商品出价" + tmp.price + "，再过30s该拍卖室关闭";
						data2 = reply.getBytes();
						for (int i = 0; i < tmp.bidderList.size(); i++) {
							packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
									tmp.bidderList.get(i).port);
							socket.send(packet2);
						}
						break;
					} else if (price > tmp.price) {
						utmp = emperor.get(id);
						tmp.price = price;
						tmp.bidderID = id;
						tmp.biddername = utmp.biddername;
						tmp.countDown = true;
						reply = "14, 用户" + tmp.biddername + "为商品出价" + tmp.price + "，再过30s该拍卖室关闭";
						data2 = reply.getBytes();
						for (int i = 0; i < tmp.bidderList.size(); i++) {
							packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
									tmp.bidderList.get(i).port);
							socket.send(packet2);
						}
						break;
					}
				}
				reply = "14, 该竞拍已结束或出价不合理";
				data2 = reply.getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				socket.send(packet2);
				break;
			}

			// leave
			case 6: {
				if (order.length == 3) {
					int no = Integer.parseInt(order[1]);
					int id = Integer.parseInt(order[2]);
					tmp = magican.get(no);
					utmp = emperor.get(id);
					if (tmp.bidderID == id) {
						reply = "14, 离开失败，你是当前的最高出价者";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else {
						reply = "8, 用户" + utmp.biddername + "离开拍卖室";
						data2 = reply.getBytes();
						for (int i = 0; i < tmp.bidderList.size(); i++) {
							if (tmp.bidderList.get(i).id == id){
								tmp.bidderList.remove(i);
								tmp.bidderNum--;
								reply = "8, 用户" + utmp.biddername + "离开拍卖室";
								data2 = reply.getBytes();
								for (int j = 0; j < tmp.bidderList.size(); j++){
									packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(j).address,
											tmp.bidderList.get(j).port);
									socket.send(packet2);
								}
								break;
							}							
						}
						reply = "6, 离开成功";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					}
				}
				break;
			}

			// msg
			case 8: {
				if (order.length == 2) {
					for (int i = 0; i < emperor.size(); i++) {
						utmp = emperor.get(i);
						reply = "8, 管理员信息：" + order[1];
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, utmp.address, utmp.port);
						socket.send(packet2);
					}
					reply = "8, 群发信息成功";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				} else {
					int id = Integer.parseInt(order[1]);
					if (id < emperor.size()) {
						utmp = emperor.get(id);
						reply = "8, 管理员信息：" + order[2];
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, utmp.address, utmp.port);
						socket.send(packet2);
						reply = "8, 发送信息成功";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					} else {
						reply = "14, 查无此用户";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					}
				}
			}

			// enter
			case 9: {
				int no = Integer.parseInt(order[1]);
				if (no < magican.size()) {
					if (magican.get(no).flag) {
						reply = "9, 进入竞拍室成功, " + no;
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					}
				}
				reply = "14, 查无此竞拍室或该竞拍已结束";
				data2 = reply.getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				socket.send(packet2);
				break;
			}

			// kickout
			case 10: {
				boolean flag = true;
				int id = Integer.parseInt(order[1]);
				int no = Integer.parseInt(order[2]);
				if (no < magican.size()) {
					if (magican.get(no).flag) {
						tmp = magican.get(no);
						for (int i = 0; i < tmp.bidderList.size(); i++) {
							if (id == tmp.bidderList.get(i).id && id != tmp.bidderID) {
								flag = false;
								utmp = tmp.bidderList.get(i);
								tmp.bidderList.remove(i);
								tmp.bidderNum--;
								reply = "6, 抱歉，您已被请出该竞拍室";
								data2 = reply.getBytes();
								packet2 = new DatagramPacket(data2, data2.length, utmp.address, utmp.port);
								socket.send(packet2);
								reply = "8, 用户" + utmp.biddername + "离开拍卖室";
								data2 = reply.getBytes();
								for (int j = 0; j < tmp.bidderList.size(); j++){
									packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(j).address,
											tmp.bidderList.get(j).port);
									socket.send(packet2);
								}
								reply = "10, 踢出用户成功";
								data2 = reply.getBytes();
								packet2 = new DatagramPacket(data2, data2.length, address, port);
								socket.send(packet2);
								break;
							}
						}
					}
				}
				if (flag) {
					reply = "14, 用户是当前的最高出价者或用户不存在或在或拍卖已结束";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
				}
				break;
			}

			// open
			case 11: {
				int price = Integer.parseInt(order[2]);
				if (price > 0) {
					tmp = new Auction(label, order[1], price);
					magican.add(label, tmp);
					label++;
					reply = "11, 创建新的竞拍室成功";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				}

				reply = "14, 价格不合理";
				data2 = reply.getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				socket.send(packet2);
				break;
			}

			// close
			case 12: {
				int no = Integer.parseInt(order[1]);
				if (no < magican.size()) {
					if (magican.get(no).flag) {
						tmp = magican.get(no);
						if (tmp.bidderNum != 0) {
							reply = "6, 竞拍室关闭";
							data2 = reply.getBytes();
							for (int i = 0; i < tmp.bidderList.size(); i++) {
								packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
										tmp.bidderList.get(i).port);
								socket.send(packet2);
							}
							reply = "6, 您已购得物品" + tmp.name;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, emperor.get(tmp.bidderID).address,
									emperor.get(tmp.bidderID).port);
							socket.send(packet2);
						}
						tmp.flag = false;
						reply = "12, 关闭竞拍室成功";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						break;
					}
				}
				reply = "14, 查无此竞拍室或该竞拍已结束";
				data2 = reply.getBytes();
				packet2 = new DatagramPacket(data2, data2.length, address, port);
				break;
			}

			// quit
			case 13: {
				if (order.length == 3) {
					int no = Integer.parseInt(order[1]);
					int id = Integer.parseInt(order[2]);
					tmp = magican.get(no);
					utmp = emperor.get(id);
					if (tmp.bidderID == id) {
						reply = "14, 退出失败，你是当前的最高出价者";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else {
						reply = "8, 用户" + utmp.biddername + "离开拍卖室";
						data2 = reply.getBytes();
						for (int j = 0; j < tmp.bidderList.size(); j++) {
							if (tmp.bidderList.get(j).id == id){
								tmp.bidderList.remove(j);
								tmp.bidderNum--;
							}else{
								packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(j).address,
										tmp.bidderList.get(j).port);
							}							
						}
						reply = "13, ";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					}
				}
				break;
			}
			}
		}
	}
	
	public void startClock(Auction given) throws IOException {
		Clock counter = new Clock(given);
		empress.add(counter);
		new Thread(counter).start();
	}

	private class Clock implements Runnable {
		private int time;
		private Auction mine;

		public Clock(Auction given) throws IOException {
			time = 30;
			mine = given;
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(mine.countDown)
				{
					time=30;
					mine.countDown = false;
				}
				if (time != 0)
				{
					time--;
				}
				else {
					String info = "12 " + mine.no;
					byte[] data2 = info.getBytes();
					DatagramPacket packet2 = new DatagramPacket(data2, data2.length, myaddress, myport);
					try {
						socket.send(packet2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}

}