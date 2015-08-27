package com.nuct.model;
public class Player {
	public String name ;
	public String passwd,email,school;
	public int gender; //数据库存数字，方便转换为英文
	public int level;
	public int points;
	public int status;
	
	public Player(){}
	public Player(String name, String passwd, String email,  String school,
			int gender) {
		super();
		this.name = name;
		this.email = email;
		this.passwd = passwd;
		this.school = school;
		this.gender = gender;
	}
	
	public Player(String str){
		String cmds[] = str.split("\\$");
		this.name = cmds[0];
		this.passwd = cmds[1];
		this.email = cmds[2];
		this.school = cmds[3];
		this.gender = Integer.parseInt(cmds[4]);
		this.level = Integer.parseInt(cmds[5]);
		this.points = Integer.parseInt(cmds[6]);
	}
	
	public Player(String cmds[], int start){
		this.name = cmds[0+start];
		this.passwd = cmds[1+start];
		this.email = cmds[2+start];
		this.school = cmds[3+start];
		this.gender = Integer.parseInt(cmds[4+start]);
		this.level = Integer.parseInt(cmds[5+start]);
		this.points = Integer.parseInt(cmds[6+start]);
		this.status = Integer.parseInt(cmds[7+start]);
	}
	
	public String getStr(){
		return this.name + "$" + passwd + "$" + email + "$" + school + "$" + 
		gender + "$" + level + "$" + points;
	}
}
