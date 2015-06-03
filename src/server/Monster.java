package server;

import java.util.Random;

import common.*;

public class Monster implements MapObject{
	Coordinate c;
	int ID,Type,MaxHP,CurrentHP,Hunger;
	
	public String toString()
	{
		return Integer.toString(Type)+ " " + Integer.toString(MaxHP)+ " " + Integer.toString((int) c.X())+ " " + Integer.toString((int) c.Y())+ " " + Integer.toString(CurrentHP)+ " " + Integer.toString(Hunger);
	}
	public Monster(int ID, int Type,int MaxHP,int X,int Y,int CurrentHP,int Hunger)
	{
		this.ID = ID;
		this.Type = Type;
		this.MaxHP = MaxHP;
		this.c = new Coordinate(X,Y);
		this.CurrentHP = CurrentHP;
		this.Hunger = Hunger;
	}
	
	public int getType()
	{
		return this.Type;
	}
	
	public int getID()
	{
		return this.ID;
	}
	
	
	
	public int getMaxHP()
	{
		return this.MaxHP;
	}
	
	public int getCurrentHP()
	{
		return this.CurrentHP;
	}
	
	public int getHunger()
	{
		return this.Hunger;
	}
	
	public void setType(int Type)
	{
		this.Type = Type;
	}
	
	public void setMaxHP(int MaxHP)
	{
		this.MaxHP = MaxHP;
	}
	
	public void setCoordinate(Coordinate co)
	{
		c = new Coordinate(co);
	}
	
	public void setCurrentHP(int CurrentHP)
	{
		this.CurrentHP = CurrentHP;
	}
	
	public void setHunger(int Hunger)
	{
		this.Hunger = Hunger;
	}
	@Override
	public Coordinate Coordinates() {
		return c;
	}
	@Override
	public common.Resource Resource() {
		Random rnd = new Random();
		int resource = rnd.nextInt(2);
		if(resource == 1) return Resource.LEATHER;
		return Resource.MEAT;
	}
	@Override
	public int Health() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void Damage(int amount) {
		CurrentHP=CurrentHP-amount;
		
	}

}
