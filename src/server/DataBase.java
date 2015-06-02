/**
 * 
 */
package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import common.*;

/**
 * 
 *
 */
public class DataBase {
	private static boolean need_to_check_file=true;
	private static String schema;
	private static String user;
	private static String pass;
	 private static String get_defaults()
     {
		 if(need_to_check_file)
		 {
				String line;
				try {
					// FileReader reads text files in the default encoding.
					FileReader fileReader = new FileReader("sql.txt");

					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);

					line = bufferedReader.readLine();
					schema = new String(line);
					
					line = bufferedReader.readLine();
					user = new String(line);

					line = bufferedReader.readLine();
					pass = new String(line);
					// System.out.println(line);

					// Always close files.
					bufferedReader.close();
				} catch (FileNotFoundException ex) {
					System.out.println("Unable to open file 'sql.txt'");
					schema="test";
					user="root";
					pass="1234";
				} catch (IOException ex) {
					System.out.println("Error reading file 'sql.txt'");
					schema="test";
					user="root";
					pass="1234";
					// Or we could just do this:
					// ex.printStackTrace();
				}
				need_to_check_file=false;
		 }
         return "jdbc:mysql://localhost/"+schema;
     }
     
     public static Connection get_connection()
     {
         String connection_string = get_defaults();
         try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Connection conn = DriverManager.getConnection(connection_string,user,pass);
			return conn;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
     }
     
	/**
	 * Update current HP of monster after a fight
	 */
	public static SQLOutput UpdateMonsterHP (int ID, int damage)
	{
		try{
			SQLOutput flag = SQLOutput.EXISTS;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Update_Monster_HP(?,?,?)}");
			prc.setInt(1,ID);
			prc.setInt(2, damage);
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static SQLOutput IsMonsterDead(int ID)
	{
		try{
			SQLOutput flag = SQLOutput.EXISTS;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Is_Monster_Dead(?,?)}");
			prc.setInt(1,ID);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(2);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			else if(result == 1)
				flag = SQLOutput.YES;
			else
				flag = SQLOutput.NO;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static SQLOutput RemoveMonster(int ID)
	{
		try{
			SQLOutput flag = SQLOutput.EXISTS;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Remove_Monster(?,?)}");
			prc.setInt(1,ID);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(2);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			else
				flag = SQLOutput.EXISTS;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static int AddMonster(Monster mnst)
	{
		try{
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Add_Monster(?,?,?,?,?,?,?)}");
			prc.setInt(1,mnst.getType());
			prc.setInt(2,mnst.getMaxHP());
			prc.setInt(3,(int)mnst.Coordinates().X());
			prc.setInt(4,(int)mnst.Coordinates().Y());
			prc.setInt(5,mnst.getCurrentHP());
			prc.setInt(6,mnst.getHunger());
			prc.registerOutParameter(7, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(7);
			if(result == -1)
			{
				con.close();
				return -1;
			}
			else
			{
				con.close();
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static Monster GetMonsterByCoordinate(Coordinate cor)
	{
		try{
			long x = cor.X();
			long y = cor.Y();
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Monster_By_Coordinate(?,?,?,?,?,?,?,?)}");
			prc.registerOutParameter(1, Types.INTEGER);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.registerOutParameter(3, Types.INTEGER);
			prc.setLong(4,x);
			prc.setLong(5,y);
			prc.registerOutParameter(6, Types.INTEGER);
			prc.registerOutParameter(7, Types.INTEGER);
			prc.registerOutParameter(8, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(8);
			if(result == 0)
			{
				con.close();
				return null;
			}
			else
			{
				Monster mnst = new Monster(prc.getInt(1),prc.getInt(2),prc.getInt(3),(int)x,(int)y,prc.getInt(6),prc.getInt(7));
				con.close();
				return mnst;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Monster GetMonsterByID(int ID)
	{
		try{
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Monster_By_ID(?,?,?,?,?,?,?,?)}");
			prc.setInt(1,ID);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.registerOutParameter(3, Types.INTEGER);
			prc.registerOutParameter(4, Types.INTEGER);
			prc.registerOutParameter(5, Types.INTEGER);
			prc.registerOutParameter(6, Types.INTEGER);
			prc.registerOutParameter(7, Types.INTEGER);
			prc.registerOutParameter(8, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(8);
			if(result == 0)
			{
				con.close();
				return null;
			}
			else
			{
				Monster mnst = new Monster(ID,prc.getInt(2),prc.getInt(3),prc.getInt(4),prc.getInt(5),prc.getInt(6),prc.getInt(7));
				con.close();
				return mnst;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Coordinate GetPlayerCoordinate(int ID)
	{
		try{
			Coordinate crdnt = null;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Player_Coordinate(?,?,?,?)}");
			prc.setInt(1,ID);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.registerOutParameter(3, Types.INTEGER);
			prc.registerOutParameter(4, Types.INTEGER);
			prc.execute();
			int X = prc.getInt(2);
			int Y = prc.getInt(3);
			int Result = prc.getInt(4);
			if(Result == 0)
				return null;
			else
				crdnt = new Coordinate(X,Y);
			con.close();
			return crdnt;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Hashtable<Resource, Integer> GetInventory(int UID)
	{
		try{
			Hashtable<Resource, Integer> inv = null;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Inventory(?,?)}");
			prc.setInt(1,UID);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.execute();
			int Result = prc.getInt(2);
			ResultSet Res = prc.getResultSet();
			if(Result != 0)
			{
				inv = new Hashtable<Resource, Integer>();
				while(Res.next())
				{
					inv.put(Resource.values()[Res.getInt(1)],Res.getInt(2));
				}
			}
			Res.close();
			con.close();
			return inv;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static SQLOutput AddEqipmentToPlayer(int UID, Equipment eq)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Add_Equipment_To_Player(?,?,?)}");
			prc.setInt(1,UID);
			prc.setInt(2, eq.getID());
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			else if(result == 1)
				flag = SQLOutput.EXISTS;
			else
				flag = SQLOutput.OK;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static SQLOutput RemoveEquipmentFromPlayer(int UID, Equipment eq)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Remove_Equipment_From_Player(?,?,?)}");
			prc.setInt(1,UID);
			prc.setInt(2, eq.getID());
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			else if(result == 1)
				flag = SQLOutput.OK;
			else
				flag = SQLOutput.NO;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static List<FloorType> get_possible_neighbors(Tile t)
	{
		//TODO Stub for map generation
		List<FloorType> lst = new ArrayList<FloorType>();
		lst.add(FloorType.GRASS);
		lst.add(FloorType.DIRT);
		lst.add(FloorType.STONE);
		return lst;
	}
	
	public static List<MapObjectType> get_possible_content(Tile t)
	{
		//TODO Stub for map generation
		List<MapObjectType> lst = new ArrayList<MapObjectType>();
		lst.add(MapObjectType.BUSH);
		lst.add(MapObjectType.TREE);
		lst.add(MapObjectType.ROCK);
		lst.add(null);
		return lst;
	}
	
	public static SQLOutput AddUser(String UserName,String Password, String Salt, String eMail,String UserIMG, String ActivationCode)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Add_User(?,?,?,?,?,?,?)}");
			prc.setString(1, UserName);
			prc.setString(2, Password);
			prc.setString(3, Salt);
			prc.setString(4, eMail);
			prc.setString(5, UserIMG);
			prc.setString(6, ActivationCode);
			prc.registerOutParameter(7, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(7);
			if(result == 0)
				flag =  SQLOutput.EXISTS;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static int LoginFun(String Username , String Password )
	{
		try{
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Login(?,?,?)}");
			prc.setString(1, Username);
			prc.setString(2, Password);
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			if(result == 0)
				result = -1;
			else if(result == -1)
				result = -2;
			else if(result == -2)
				result = -3;
			con.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static SQLOutput ConfirmFun(String Username, String ActivationCode)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Change_Activity(?,?,?)}");
			prc.setString(1, Username);
			prc.setString(2, ActivationCode);
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			else if(result == 1)
				flag = SQLOutput.NO;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static void SetTile(Tile tile)
	{
		try{
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Set_Tile(?,?,?,?)}");
			prc.setLong(1, tile.getCoordinate().X());
			prc.setLong(2, tile.getCoordinate().Y());
			prc.setInt(3,tile.getFloorType().getID());
			if(tile.getMapObjectType() == null)
				prc.setInt(4,-1);
			else
				prc.setInt(4,tile.getMapObjectType().getID());
			prc.execute();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String GetSalt(String Username)
	{
		try{
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Salt(?,?)}");
			prc.setString(1, Username);
			prc.registerOutParameter(2, Types.VARCHAR);
			prc.execute();
			String str = prc.getString(2);
			con.close();
			if(str.length() == 0)
				return null;
			else
				return str;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static SQLOutput UpdatePlayerLocation (int PlayerID, Coordinate cor)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Update_Player_Location(?,?,?,?)}");
			prc.setInt(1, PlayerID);
			prc.setLong(2, cor.X());
			prc.setLong(3, cor.Y());
			prc.registerOutParameter(4, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(4);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static Tile GetTile (Coordinate cor)
	{
		try{
			Tile T = new Tile(cor);
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Tile(?,?,?,?)}");
			prc.setLong(1, cor.X());
			prc.setLong(2, cor.Y());
			prc.registerOutParameter(3, Types.INTEGER);
			prc.registerOutParameter(4, Types.INTEGER);
			prc.execute();
			int surface = prc.getInt(3);
			int object = prc.getInt(4);
			con.close();
			T.setFloorType(FloorType.values()[surface]);
			if(object == -1 || object == 0 || object == 1)
				T.setMapObjectType(null);
			else
				T.setMapObjectType(MapObjectType.values()[object]);
			return T;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Player GetPlayerByID (int UserID)
	{
		try{
			Player plr = null;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Player_By_ID(?,?)}");
			prc.setInt(1, UserID);
			prc.registerOutParameter(2, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(2);
			if(result == 1)
				plr = new Player(UserID,false);
			else if (result == 2)
				plr = new Player(UserID,true);
			con.close();
			return plr;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int GetPlayerCoordByID(Coordinate coord)
	{
		try{
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_coord_By_ID(?,?,?)}");
			prc.setLong(1, coord.X());
			prc.setLong(2, coord.Y());
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			con.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static Hashtable<Coordinate, Tile> GetMap()
	{
		try{
			Hashtable<Coordinate, Tile> Map = new Hashtable<Coordinate, Tile>();
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Map()}");
			prc.execute();
			ResultSet Res = prc.getResultSet();
			while(Res.next())
			{
				if(Res.getInt(4) != -1)
					Map.put(new Coordinate(Res.getInt(1),Res.getInt(2)),new Tile(new Coordinate(Res.getInt(1),Res.getInt(2)),FloorType.values()[Res.getInt(3)],MapObjectType.values()[Res.getInt(4)]));
				else
					Map.put(new Coordinate(Res.getInt(1),Res.getInt(2)),new Tile(new Coordinate(Res.getInt(1),Res.getInt(2)),FloorType.values()[Res.getInt(3)],null));
			}
			Res.close();
			con.close();
			return Map;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Hashtable<Coordinate, MapObject> GetMonsters()
	{
		try{
			Hashtable<Coordinate, MapObject> monst = new Hashtable<Coordinate, MapObject>();
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Get_Monsters()}");
			prc.execute();
			ResultSet Res = prc.getResultSet();
			while(Res.next())
			{
				monst.put(new Coordinate(Res.getInt(4) ,Res.getInt(5)), (MapObject) new Monster(Res.getInt(1),Res.getInt(2),Res.getInt(3),Res.getInt(4),Res.getInt(5),Res.getInt(6),Res.getInt(7)));
			}
			Res.close();
			con.close();
			return monst;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static SQLOutput MoveMonster(int ID, Coordinate coor)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Move_Monster(?,?,?,?)}");
			prc.setInt(1, ID);
			prc.setLong(2, coor.X());
			prc.setLong(3, coor.Y());
			prc.registerOutParameter(4, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(4);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static SQLOutput AddItemToInventory(int ID, Resource res)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Add_Item_To_Inventory(?,?,?)}");
			prc.setInt(1, ID);
			prc.setInt(2, res.getID());
			prc.registerOutParameter(3, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(3);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
	public static SQLOutput RemoveItemFromInventory(int ID, Resource res, int Amount)
	{
		try{
			SQLOutput flag = SQLOutput.OK;
			Connection con = get_connection();
			CallableStatement prc = con.prepareCall("{call Remove_Item_From_Inventory(?,?,?,?)}");
			prc.setInt(1, ID);
			prc.setInt(2, res.getID());
			prc.setInt(3, Amount);
			prc.registerOutParameter(4, Types.INTEGER);
			prc.execute();
			int result = prc.getInt(4);
			if(result == 0)
				flag =  SQLOutput.NOT_FOUND;
			else if(result == 1)
				flag = SQLOutput.NO;
			else if(result == 2)
				flag = SQLOutput.EXISTS;
			con.close();
			return flag;
		} catch (SQLException e) {
			e.printStackTrace();
			return SQLOutput.SQL_ERROR;
		}
	}
	
}

