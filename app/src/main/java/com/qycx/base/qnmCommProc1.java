



package com.qycx.base;
public class qnmCommProc1
{
	public  static class QNM_DB_CFG
	{

		public String sysName;//[64 + 1];
		public int iSystemId;                                  //  系统id
		public int iAppType;                                   //  应用类型

		public boolean bDefault;
		//  char									dsnName[MAX_QY_DSNNAMELEN + 1];
		public int iDbType;                                    //  2006/05/02
														//
		public int iDbConnType;                                //  2012/07/12. 数据库连接方式
														//  
		public boolean ucbDsnless;                                   //  2007/06/13,  采用无DSN的连接方式
			
		 //
		public String dbServIp;//[CONST_qyMaxIpLen + 1];
		public boolean ucbUseWinAuth;                                //  2014/02/07,  缺省useWinAuth = FALSE;
																  //  WCHAR										dbName[64  +  1];
		public String dbName;//[MAX_PATH + 1];                     //  2007/06/13, 对无DNS的方式，此变量存储文件名，故空间要长一些
		public String uid;//[64 + 1];
		public String pwd;//[64 + 1];
		public String connectStr;//[CONST_maxConnectStrLen + 1];
		public int ulOpenOptions;
		

	}
	;

	public  static class QY_OBJ_DB
	{
		public int iType;
		//
		public QNM_DB_CFG cfg;
		//
		public int iDsnIndex;                                  //  数据源在procedObjs中的Index，在这里也为惟一的标识数据源，也用来标识在注册表中的序号，在表示添加附加的管理单元时才用，系统本身的主数据库设置为0
														//
		public boolean ucbUsed;                                  //  是否被使用
															  //
															  //public Object pDb;
		public Object pDb;
		public boolean ucbLoaded;                                    //  看是否装载，2006/12/21

		//Object pHashTbl_tree;

	}
	;


	public static class qyFuncs
    {
		public static  boolean bObjDbAvail(qnmCommProc1.QY_OBJ_DB pObj)
		{
			//if (null==pObj || !pObj->ucbLoaded) return false;
			if (!pObj.ucbLoaded) return false;
			return true;
		}

	}

}
