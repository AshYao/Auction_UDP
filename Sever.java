import java.io.*;
import java.net.*;
import java.util.*;

/*
 * �������ˣ��ṩ���ݴ洢����Ӧ����
 */
public class Sever {
	// ��������ʱ�߳�Clock��List
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

	// ������Auction��List
	public static List<Auction> magican;

	public static void main(String[] args) throws IOException {

		magican = new ArrayList<Auction>();
		List<Bidder> emperor = new ArrayList<Bidder>();
		List<Master> hierophant = new ArrayList<Master>();

		// ��ͻ�����ͬ��ָ���б���ʵ������
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

		// ��ʼ����Ʒ
		int label = 0;
		Auction tmp = new Auction(label, "The Chariot", 7);
		magican.add(label, tmp);
		label++;

		tmp = new Auction(label, "Wheel of Fortune", 10);
		magican.add(label, tmp);
		label++;

		// ��ʼ���û�
		int ulabel = 0;
		Bidder utmp = new Bidder(ulabel, "1", "1");
		emperor.add(ulabel, utmp);
		ulabel++;
		utmp = new Bidder(ulabel, "2", "2");
		emperor.add(ulabel, utmp);
		ulabel++;

		// ��ʼ������Ա
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
		
		System.out.println("****���������Ѿ�����****");

		while (true) {
			socket.receive(packet);
			info = new String(data, 0, packet.getLength());
			address = packet.getAddress();
			port = packet.getPort();

			// ��ȡָ��
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
							reply = "14, �û����ѱ�ʹ��";
							flag = false;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, address, port);
							socket.send(packet2);
							break;
						}
					}
				}
				if (flag) {
					reply = "0, ע��ɹ�";
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
							reply = "1, ��½�ɹ����𾴵��û�, " + emperor.get(i).id;
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
					reply = "14, �û������������";
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
							reply = "7, ��½�ɹ�������Ա���, " + hierophant.get(i).id;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, address, port);
							socket.send(packet2);
							break;
						}
					}
				}
				if (flag) {
					reply = "14, �û������������";
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
							reply = "8, �û�" + utmp.biddername + "����������";
							data2 = reply.getBytes();
							for (int i = 0; i < tmp.bidderList.size(); i++) {
								packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
										tmp.bidderList.get(i).port);
								socket.send(packet2);
							}
						}
						tmp.bidderList.add(tmp.bidderNum, utmp);
						tmp.bidderNum++;
						reply = "3, ���뾺���ҳɹ�, " + no;
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					}
				}
					reply = "14, ���޴˾����һ�þ����ѽ���";
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
						reply = "14, �û�" + tmp.biddername + "Ϊ��Ʒ����" + tmp.price + "���ٹ�30s�������ҹر�";
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
						reply = "14, �û�" + tmp.biddername + "Ϊ��Ʒ����" + tmp.price + "���ٹ�30s�������ҹر�";
						data2 = reply.getBytes();
						for (int i = 0; i < tmp.bidderList.size(); i++) {
							packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
									tmp.bidderList.get(i).port);
							socket.send(packet2);
						}
						break;
					}
				}
				reply = "14, �þ����ѽ�������۲�����";
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
						reply = "14, �뿪ʧ�ܣ����ǵ�ǰ����߳�����";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else {
						reply = "8, �û�" + utmp.biddername + "�뿪������";
						data2 = reply.getBytes();
						for (int i = 0; i < tmp.bidderList.size(); i++) {
							if (tmp.bidderList.get(i).id == id){
								tmp.bidderList.remove(i);
								tmp.bidderNum--;
								reply = "8, �û�" + utmp.biddername + "�뿪������";
								data2 = reply.getBytes();
								for (int j = 0; j < tmp.bidderList.size(); j++){
									packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(j).address,
											tmp.bidderList.get(j).port);
									socket.send(packet2);
								}
								break;
							}							
						}
						reply = "6, �뿪�ɹ�";
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
						reply = "8, ����Ա��Ϣ��" + order[1];
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, utmp.address, utmp.port);
						socket.send(packet2);
					}
					reply = "8, Ⱥ����Ϣ�ɹ�";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				} else {
					int id = Integer.parseInt(order[1]);
					if (id < emperor.size()) {
						utmp = emperor.get(id);
						reply = "8, ����Ա��Ϣ��" + order[2];
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, utmp.address, utmp.port);
						socket.send(packet2);
						reply = "8, ������Ϣ�ɹ�";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					} else {
						reply = "14, ���޴��û�";
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
						reply = "9, ���뾺���ҳɹ�, " + no;
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
						break;
					}
				}
				reply = "14, ���޴˾����һ�þ����ѽ���";
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
								reply = "6, ��Ǹ�����ѱ�����þ�����";
								data2 = reply.getBytes();
								packet2 = new DatagramPacket(data2, data2.length, utmp.address, utmp.port);
								socket.send(packet2);
								reply = "8, �û�" + utmp.biddername + "�뿪������";
								data2 = reply.getBytes();
								for (int j = 0; j < tmp.bidderList.size(); j++){
									packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(j).address,
											tmp.bidderList.get(j).port);
									socket.send(packet2);
								}
								reply = "10, �߳��û��ɹ�";
								data2 = reply.getBytes();
								packet2 = new DatagramPacket(data2, data2.length, address, port);
								socket.send(packet2);
								break;
							}
						}
					}
				}
				if (flag) {
					reply = "14, �û��ǵ�ǰ����߳����߻��û������ڻ��ڻ������ѽ���";
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
					reply = "11, �����µľ����ҳɹ�";
					data2 = reply.getBytes();
					packet2 = new DatagramPacket(data2, data2.length, address, port);
					socket.send(packet2);
					break;
				}

				reply = "14, �۸񲻺���";
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
							reply = "6, �����ҹر�";
							data2 = reply.getBytes();
							for (int i = 0; i < tmp.bidderList.size(); i++) {
								packet2 = new DatagramPacket(data2, data2.length, tmp.bidderList.get(i).address,
										tmp.bidderList.get(i).port);
								socket.send(packet2);
							}
							reply = "6, ���ѹ�����Ʒ" + tmp.name;
							data2 = reply.getBytes();
							packet2 = new DatagramPacket(data2, data2.length, emperor.get(tmp.bidderID).address,
									emperor.get(tmp.bidderID).port);
							socket.send(packet2);
						}
						tmp.flag = false;
						reply = "12, �رվ����ҳɹ�";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						break;
					}
				}
				reply = "14, ���޴˾����һ�þ����ѽ���";
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
						reply = "14, �˳�ʧ�ܣ����ǵ�ǰ����߳�����";
						data2 = reply.getBytes();
						packet2 = new DatagramPacket(data2, data2.length, address, port);
						socket.send(packet2);
					} else {
						reply = "8, �û�" + utmp.biddername + "�뿪������";
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