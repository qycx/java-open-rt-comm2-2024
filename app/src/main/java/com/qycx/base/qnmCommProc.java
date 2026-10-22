


package com.qycx.base;

import static com.qycx.base.Consts.CONST_langId_PRC;
import static com.qycx.base.Consts.CONST_maxCntAddrLen;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.commonSock.qyFuncs.qyRecvEx;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_bb;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_bm;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_bz1;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_bz2;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_dw;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_jqlx;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_lxdh;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_qtdw;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_sydd;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_syr;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_yhlx;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zb;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zcbh0;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zcbh1;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zcbh2;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zcsm1;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zcsm2;
import static com.qycx.base.qnmCommProc.Consts.CONST_fieldId_zyyt;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_char;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_data;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_l64;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_lData;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_long;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_short;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_str;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_utf8Str;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_base;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_end;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_user;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyCommFunc.Consts.CONST_maxReqDataLen_enc_netMc;
import static com.qycx.base.qyCommFunc.qyFuncs.qySendReq;
import static com.qycx.base.qyCommFunc.qyFuncs.qyTellService;
import static com.qycx.base.qyCommonFunc9.qyFuncs.qyntohs;
import static com.qycx.base.qyDefs.Consts.CONST_qyMaxVerLen;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_qwm;
import static com.qycx.base.qyFuncs.Consts.CONST_qyMaxIpLen;
import static com.qycx.base.qyMcMainDaemon_isCli.Consts.bufSize_4096;
import static com.qycx.base.qyWmComm.Consts.CONST_Status_Window_Function_Open;
import static com.qycx.base.tmpFunc.qyFuncs.mytoString;
import static com.qycx.base.tmpFunc.qyFuncs.safeStrnCpy;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class qnmCommProc {
//    private static final String TAG = qnmCommProc.class.getSimpleName();

    public static class Consts {

        //
        public final static short CONST_stateType_sys = 1;
        //
        public final static short CONST_stateType_tasks = 10;
        public final static short CONST_stateType_players = 11;
        public final static short CONST_stateType_talks = 12;
        public final static short CONST_stateType_qs = 13;
        //
        public final static short CONST_stateType_others = 20;


        //  ÒÔÏÂÊÇÊý¾ÝÀàÐÍ, ÓÃÔÚÍ¨ÐÅÀï£¬ÑÏ½ûÐÞ¸Ä
        public final static short CONST_qyDataType_l32 = 1;             //  netMcÀïÒÑÊ¹ÓÃ
        public final static short CONST_qyDataType_long = CONST_qyDataType_l32;
        public final static short CONST_qyDataType_str = 2;              //  netMcÀïÒÑÊ¹ÓÃ, ´ËÀàÐÍÊµ¼Ê·ÇÒÔ0½ØÎ²µÄ×Ö·û´®¡£Êµ¼ÊÊÇÒ»¸ö¶þ½øÖÆÁ÷¡£ÒòÎªÄÚÍø¹ÜÀíÒÑ¾­Ê¹ÓÃ£¬¹ÊÔÝÊ±²»¸Ä¶¯
        //
//        public final static short CONST_qyDataType_wStr = 3;             //  2007/03/31
        public final static short CONST_qyDataType_utf8Str = 4;              //  2007/03/31
        public final static short CONST_qyDataType_data = 5;             //  2007/03/31, Êý¾Ý³¤¶ÈÓÃshortÀ´±íÊ¾.
        public final static short CONST_qyDataType_l64 = 6;              //  2007/08/01
        public final static short CONST_qyDataType_byte = 7;             //  2007/08/05
        public final static short CONST_qyDataType_char = CONST_qyDataType_byte;             //  2007/08/05
        public final static short CONST_qyDataType_short = 8;                //  2007/08/05
        public final static short CONST_qyDataType_lData = 9;                //  2008/04/01, Õâ¸öÀàÐÍ¿ÉÌá¹©¸ü³¤µÄÊý¾ÝÃèÊö¡£Êý¾Ý³¤¶ÈÓÃlongÀ´±íÊ¾

        //
        public final static int bufSize_cfgItemData = 32 * 1024;     //  2007/03/31, ½«»º³åµÄ³¤¶È±ä³¤ÁË
        public final static int bufSize_cfgItemLData = 1024 * 1024;  //  2008/03/22

        public final static short CONST_cntof_cfgItemStr = 4096;
        public final static short CONST_cntof_cfgItemWstr = 4096;

        //
        public final static short CONST_qnmCfgId_null = 0;                   //  È¡cfgsµÄ½áÊø·û

        // 2007/04/02
        public final static short CONST_qnmCfgId_base_common = 1000;
        //
        //  CONST_qnmCfgId_base_common  ->  CONST_qnmCfgId_base_common  +  999
        public final static short CONST_qnmCfgId_start = (CONST_qnmCfgId_base_common + 100);
        public final static short CONST_qnmCfgId_mem = (CONST_qnmCfgId_base_common + 101);               //  2007/08/05, 只用在小而快的数据包。一般的包的成员要使用CONST_imCommType_mem
        //
        //

        //
        //
        //
        //
        public final static short CONST_qnmCfgId_clientVer = (CONST_qnmCfgId_base_common + 2009);
        public final static short CONST_qnmCfgId_authCmd = (CONST_qnmCfgId_base_common + 2010);
        //  public final static int 		CONST_qnmCfgId_uiQwmType							(  CONST_qnmCfgId_base_common  +  2011  )
        public final static short CONST_qnmCfgId_uiObjType = (CONST_qnmCfgId_base_common + 2011);                //  2007/12/07, qwmType¸objType
        public final static short CONST_qnmCfgId_iCustemId = (CONST_qnmCfgId_base_common + 2012);
        public final static short CONST_qnmCfgId_uiChannelType = (CONST_qnmCfgId_base_common + 2013);                //  2007/07/08
        //  public final static int 		CONST_qnmCfgId_uiDevType						=(  CONST_qnmCfgId_base_common  +  2014  );				//  2007/08/03, messenger
        public final static short CONST_qnmCfgId_uiDevType_from = (CONST_qnmCfgId_base_common + 2014);               //  2007/08/03, messenger
        public final static short CONST_qnmCfgId_uiDevType_to = (CONST_qnmCfgId_base_common + 2015);             //  2008/01/06, messenger
        public final static short CONST_qnmCfgId_wDevIdStr_from = (CONST_qnmCfgId_base_common + 2016);               //  2008/01/06, from from uiDevType_from uiDevType
        public final static short CONST_qnmCfgId_wDevIdStr_to = (CONST_qnmCfgId_base_common + 2017);             //  2008/01/06
        public final static short CONST_qnmCfgId_wReversePath = (CONST_qnmCfgId_base_common + 2018);             //  2008/02/08
        public final static short CONST_qnmCfgId_wForwardPath = (CONST_qnmCfgId_base_common + 2019);             //  2008/02/08
        public final static short CONST_qnmCfgId_usAuthType = (CONST_qnmCfgId_base_common + 2020);               //  2011/03/22
        public final static short CONST_qnmCfgId_iAppType = (CONST_qnmCfgId_base_common + 2021);             //  2011/03/27
        //
        public final static short CONST_qnmCfgId_shVer = (CONST_qnmCfgId_base_common + 2023);                //  2014/09/23
        public final static short CONST_qnmCfgId_subSystemId = (CONST_qnmCfgId_base_common + 2024);              //  2017/06/21

        //
        public final static short CONST_qnmCfgId_uiFieldId = (CONST_qnmCfgId_base_common + 2100);                //  2007/07/31
        public final static short CONST_qnmCfgId_usCnt = (CONST_qnmCfgId_base_common + 2101);                //  2007/08/01
        public final static short CONST_qnmCfgId_ucbNext = (CONST_qnmCfgId_base_common + 2102);              //  2007/08/03
        public final static short CONST_qnmCfgId_ucbEnd = (CONST_qnmCfgId_base_common + 2103);               //  2007/08/03
        //public final static int 		CONST_qnmCfgId_tStartTime_serv						=(  CONST_qnmCfgId_base_common  +  2104  );				//  tStartTime_serv uiSerialNo
        public final static short CONST_qnmCfgId_uiSerialNo = (CONST_qnmCfgId_base_common + 2105);               //  2007/09/01
        public final static short CONST_qnmCfgId_ucbResp = (CONST_qnmCfgId_base_common + 2106);              //  2007/12/22,
        public final static short CONST_qnmCfgId_ucbNeedDetail = (CONST_qnmCfgId_base_common + 2107);                //  2007/12/22,
        public final static short CONST_qnmCfgId_ucbNeedResp = (CONST_qnmCfgId_base_common + 2108);              //  2008/04/21,
        public final static short CONST_qnmCfgId_index = (CONST_qnmCfgId_base_common + 2109);                //  2008/10/30. 这里的index应理解为某项数据的唯一的标识，不应理解为数组的索引。在网络协议里，数组的索引没有意义。
        public final static short CONST_qnmCfgId_uiLen = (CONST_qnmCfgId_base_common + 2110);                //  2008/10/30
        public final static short CONST_qnmCfgId_ucbRetrieveAll = (CONST_qnmCfgId_base_common + 2111);               //  2010/08/17
        //
        public final static short CONST_qnmCfgId_subtype = (CONST_qnmCfgId_base_common + 2112);              //  2011/01/28
        public final static short CONST_qnmCfgId_ucbReply = (CONST_qnmCfgId_base_common + 2113);             //  2012/08/06
        public final static short CONST_qnmCfgId_ucbRetrieveOne = (CONST_qnmCfgId_base_common + 2114);               //  2014/09/04.
        public final static short CONST_qnmCfgId_help_subIndex = (CONST_qnmCfgId_base_common + 2115);                //  2016/07/23
        //
        //public final static int  CONST_qnmCfgId_ucbAvConsole							=(  CONST_qnmCfgId_base_common  +  2116  );				//  2017/06/11
        //
        public final static short CONST_qnmCfgId_ucbVideoConference = (CONST_qnmCfgId_base_common + 2117);               //  2017/08/02
        public final static short CONST_qnmCfgId_usConfType = (CONST_qnmCfgId_base_common + 2118);               //  2017/08/25
        public final static short CONST_qnmCfgId_usAvLevel = (CONST_qnmCfgId_base_common + 2119);                //  2011/08/15

        //
        //
        public final static short CONST_qnmCfgId_mac = (CONST_qnmCfgId_base_common + 2300);              //
        public final static short CONST_qnmCfgId_ulIp = (CONST_qnmCfgId_base_common + 2301);             //  ulIp
        public final static short CONST_qnmCfgId_ip6 = (CONST_qnmCfgId_base_common + 2302);              //  reserved
        public final static short CONST_qnmCfgId_pcName = (CONST_qnmCfgId_base_common + 2303);
        public final static short CONST_qnmCfgId_domainName = (CONST_qnmCfgId_base_common + 2304);
        public final static short CONST_qnmCfgId_osUsrName = (CONST_qnmCfgId_base_common + 2305);
        public final static short CONST_qnmCfgId_langId = (CONST_qnmCfgId_base_common + 2306);
        public final static short CONST_qnmCfgId_iPlatformId = (CONST_qnmCfgId_base_common + 2307);
        public final static short CONST_qnmCfgId_categoryId = (CONST_qnmCfgId_base_common + 2308);               //  2011/04/05

        //
        public final static short CONST_qnmCfgId_hint = (CONST_qnmCfgId_base_common + 2400);             //  2007/04/22
        public final static short CONST_qnmCfgId_clientWelcomeMsg = (CONST_qnmCfgId_base_common + 2401);             //  2016/08/10
        //
        public final static short CONST_qnmCfgId_ucbDaemonDemo = (CONST_qnmCfgId_base_common + 2410);                //  2009/10/11
        public final static short CONST_qnmCfgId_usMaxVideoConferenceMems = (CONST_qnmCfgId_base_common + 2411);             //  2009/11/05
        public final static short CONST_qnmCfgId_ucDays_left = (CONST_qnmCfgId_base_common + 2412);              //  2010/03/22
        //
        public final static short CONST_qnmCfgId_uiCommEncCtxType = (CONST_qnmCfgId_base_common + 2450);             //  2008/ 06/09
        public final static short CONST_qnmCfgId_publicKeyBlob = (CONST_qnmCfgId_base_common + 2451);                //  2008/06/09
        public final static short CONST_qnmCfgId_sessionKeyBlob = (CONST_qnmCfgId_base_common + 2452);             //  2008/06/10
        public final static short CONST_qnmCfgId_usSessionEncType = (CONST_qnmCfgId_base_common + 2453);               //  2008/06/10
        public final static short CONST_qnmCfgId_sessionIv = (CONST_qnmCfgId_base_common + 2454);               //  2008/06/10

        //
        public final static short CONST_qnmCfgId_messengerId = (CONST_qnmCfgId_base_common + 2500);
        public final static short CONST_qnmCfgId_idInfo_grp1 = (CONST_qnmCfgId_base_common + 2501);              //  2007/08/26
        //
        public final static short CONST_qnmCfgId_idInfo_logicalPeer = (CONST_qnmCfgId_base_common + 2502);               //  file server  需要使用logicalPeer的概念。其他的应该使用grp_related.
        //
        public final static short CONST_qnmCfgId_idInfo_grp_related = (CONST_qnmCfgId_base_common + 2503);               //  2015/07/30.
        public final static short CONST_qnmCfgId_idInfo_resServ = (CONST_qnmCfgId_base_common + 2504);               //  2015/08/01
        public final static short CONST_qnmCfgId_idInfo_sender = (CONST_qnmCfgId_base_common + 2505);                //  2015/08/07
        public final static short CONST_qnmCfgId_idInfo_creator = (CONST_qnmCfgId_base_common + 2506);               //  2017/07/27
        public final static short CONST_qnmCfgId_idInfo_initiator = (CONST_qnmCfgId_base_common + 2507);
        public final static short CONST_qnmCfgId_idInfo_mcu = (CONST_qnmCfgId_base_common + 2508);
        public final static short CONST_qnmCfgId_idInfo_conf = (CONST_qnmCfgId_base_common + 2509);
        public final static short CONST_qnmCfgId_idInfo_compere = (CONST_qnmCfgId_base_common + 2510);
        //
        public final static short CONST_qnmCfgId_idInfo_requester = (CONST_qnmCfgId_base_common + 2511);


        public final static short CONST_qnmCfgId_idInfo_nvr = (CONST_qnmCfgId_base_common + 2512);


        //
        public final static short CONST_qnmCfgId_messengerName = (CONST_qnmCfgId_base_common + 2610);                //  messengerName
        public final static short CONST_qnmCfgId_messengerPasswd = (CONST_qnmCfgId_base_common + 2611);              //  messengerPassword
        //
        public final static short CONST_qnmCfgId_wRndName_serv = (CONST_qnmCfgId_base_common + 2616);                //  2007/11/22
        public final static short CONST_qnmCfgId_uiRnd_cli = (CONST_qnmCfgId_base_common + 2617);                //  2007/11/22
        //  public final static int 		CONST_qnmCfgId_uiWebSessId						=(  CONST_qnmCfgId_base_common  +  2618  );				//  2007/11/22
        public final static short CONST_qnmCfgId_uiLogonId = (CONST_qnmCfgId_base_common + 2618);                //  2007/11/22
        //
        public final static short CONST_qnmCfgId_grpDisplayName = (CONST_qnmCfgId_base_common + 2620);               //  2007/11/22
        public final static short CONST_qnmCfgId_displayName = (CONST_qnmCfgId_base_common + 2621);              //  2007/11/22
        public final static short CONST_qnmCfgId_wAdvertisement = (CONST_qnmCfgId_base_common + 2622);               //  2008/03/02
        public final static short CONST_qnmCfgId_talkerDesc = (CONST_qnmCfgId_base_common + 2623);               //  2011/04/07
        //
        //  MSG_ROUTE
        public final static short CONST_qnmCfgId_messengerId_from = (CONST_qnmCfgId_base_common + 2640);             //  2007/11/29
        public final static short CONST_qnmCfgId_messengerId_to = (CONST_qnmCfgId_base_common + 2641);               //  2007/11/29
        public final static short CONST_qnmCfgId_messengerId_replyTo = (CONST_qnmCfgId_base_common + 2642);              //  2007/11/29
        //
        public final static short CONST_qnmCfgId_uiMsgRouteId = (CONST_qnmCfgId_base_common + 2650);             //  2008/05/27

        //
        //  3000 - 3499
        //
        public final static short CONST_qnmCfgId_tCreationTime = (CONST_qnmCfgId_base_common + 3098);                //  2015/08/12
        //
        public final static short CONST_qnmCfgId_tLastModifiedTime = (CONST_qnmCfgId_base_common + 3099);                //  2009/08/24
        public final static short CONST_qnmCfgId_tLastRefreshedTime = (CONST_qnmCfgId_base_common + 3100);
        public final static short CONST_qnmCfgId_idInfo_lastRefreshed = (CONST_qnmCfgId_base_common + 3101);
        public final static short CONST_qnmCfgId_idInfo_grp_lastRefreshed = (CONST_qnmCfgId_base_common + 3102);             //  2007/08/26, ½ÇÉ«
        //
        public final static short CONST_qnmCfgId_usRunningStatus = (CONST_qnmCfgId_base_common + 3110);
        public final static short CONST_qnmCfgId_ulDetectedIp = (CONST_qnmCfgId_base_common + 3111);
        public final static short CONST_qnmCfgId_tLastCommTime = (CONST_qnmCfgId_base_common + 3112);                //  2007/12/23, webMessengerÓÃµÄ£®
        public final static short CONST_qnmCfgId_wLocation = (CONST_qnmCfgId_base_common + 3113);                //  2007/12/23, webMessengerÓÃµÄ£¬±íIP¶ÔÓ¦µÄµØ·½£®
        //
        public final static short CONST_qnmCfgId_usMaxMemsPerSnd = (CONST_qnmCfgId_base_common + 3120);              //  2007/08/26
        public final static short CONST_qnmCfgId_iRole = (CONST_qnmCfgId_base_common + 3122);                //  2007/08/26, ½ÇÉ«


        // 3500 - 3999
        public final static short CONST_qnmCfgId_ui64FileLen = (CONST_qnmCfgId_base_common + 3500);              //  2007/09/03
        public final static short CONST_qnmCfgId_fileName = (CONST_qnmCfgId_base_common + 3501);             //  2007/09/03
        public final static short CONST_qnmCfgId_usOp = (CONST_qnmCfgId_base_common + 3502);             //  2007/09/03
        public final static short CONST_qnmCfgId_tStartTime_org = (CONST_qnmCfgId_base_common + 3503);               //  2007/09/03
        public final static short CONST_qnmCfgId_uiTranNo_org = (CONST_qnmCfgId_base_common + 3504);             //  2007/09/03
        public final static short CONST_qnmCfgId_uiSerialNo_org = (CONST_qnmCfgId_base_common + 3505);               //  2007/09/03
        public final static short CONST_qnmCfgId_uiSizePerSnd = (CONST_qnmCfgId_base_common + 3506);             //  2007/09/03
        public final static short CONST_qnmCfgId_uiDataBlockIndex = (CONST_qnmCfgId_base_common + 3507);             //  2007/09/04
        //  public final static int 		CONST_qnmCfgId_fileData								=(  CONST_qnmCfgId_base_common  +  3508  );				//  2007/09/04
        public final static short CONST_qnmCfgId_rawData = (CONST_qnmCfgId_base_common + 3508);              //  2008/03/23
        public final static short CONST_qnmCfgId_uiVerifyMethod = (CONST_qnmCfgId_base_common + 3509);               //  2007/09/04
        public final static short CONST_qnmCfgId_uiContentType_org = (CONST_qnmCfgId_base_common + 3510);                //  2008/05/31
        public final static short CONST_qnmCfgId_usCmd_org = (CONST_qnmCfgId_base_common + 3511);                //  2009/10/19
        public final static short CONST_qnmCfgId_usOp_org = (CONST_qnmCfgId_base_common + 3512);             //  2009/10/19
        //
        public final static short CONST_qnmCfgId_ucbSaveVideo = (CONST_qnmCfgId_base_common + 3513);             //  2014/08/04. 这个值其实不应该放在这里.

        //
        public final static short CONST_qnmCfgId_usIntervalInS_refreshRecentFriends = (CONST_qnmCfgId_base_common + 3530);               //  2007/09/20
        public final static short CONST_qnmCfgId_usIntervalInS_refreshContactList = (CONST_qnmCfgId_base_common + 3531);             //  2007/09/20


        // 4000 - 4999
        public final static short CONST_qnmCfgId_usMaxFps_toShareBmp = (CONST_qnmCfgId_base_common + 4035);              //  2014/08/07
        //
        //public final static int 		CONST_qnmCfgId_usCntLimit_mems_from					=(  CONST_qnmCfgId_base_common  +  4036  );				//  2009/05/30
        public final static short CONST_qnmCfgId_uiBytesRecorded_perBlockAlign = (CONST_qnmCfgId_base_common + 4037);                //  2009/05/24
        public final static short CONST_qnmCfgId_usMaxFps_toShareBmp_conf = (CONST_qnmCfgId_base_common + 4038);             //  2009/04/19
        public final static short CONST_qnmCfgId_ucVideoCompressors = (CONST_qnmCfgId_base_common + 4039);               //  2009/03/30
        public final static short CONST_qnmCfgId_uiTaskType = (CONST_qnmCfgId_base_common + 4040);               //  2007/10/07							//
        public final static short CONST_qnmCfgId_ulIp_caller = (CONST_qnmCfgId_base_common + 4041);              //  IP
        public final static short CONST_qnmCfgId_usCapDriverIndex = (CONST_qnmCfgId_base_common + 4042);             //  2008/03/23
        //public final static int  CONST_qnmCfgId_fourccStr							=(  CONST_qnmCfgId_base_common  +  4043  );				//
        public final static short CONST_qnmCfgId_iFourcc = (CONST_qnmCfgId_base_common + 4043);                //
        public final static short CONST_qnmCfgId_bih_compress = (CONST_qnmCfgId_base_common + 4044);             //  bitmapInfoHeader
        public final static short CONST_qnmCfgId_bih_decompress = (CONST_qnmCfgId_base_common + 4045);               //  bitmapInfoHeader
        public final static short CONST_qnmCfgId_usFps = (CONST_qnmCfgId_base_common + 4046);                //
        public final static short CONST_qnmCfgId_wf_compress = (CONST_qnmCfgId_base_common + 4047);              //  2008/06/15
        public final static short CONST_qnmCfgId_wf_decompress = (CONST_qnmCfgId_base_common + 4048);                //  2009/04/27
        public final static short CONST_qnmCfgId_ucAudioCompressors = (CONST_qnmCfgId_base_common + 4049);               //  2009/05/17
        public final static short CONST_qnmCfgId_uiTranNo_openVideoDev = (CONST_qnmCfgId_base_common + 4050);                //
        public final static short CONST_qnmCfgId_uiTranNo_openAudioDev = (CONST_qnmCfgId_base_common + 4051);                //
        //public final static int 		CONST_qnmCfgId_ucbFlippedImg						=(  CONST_qnmCfgId_base_common  +  4052  );				//  2011/12/11
        public final static short CONST_qnmCfgId_spsPps = (CONST_qnmCfgId_base_common + 4053);               //  2014/03/30
        //
        public final static short CONST_qnmCfgId_uiSampleTimeInMs = (CONST_qnmCfgId_base_common + 4054);             //  2009/05/04
        public final static short CONST_qnmCfgId_ui_rtTimeLen = (CONST_qnmCfgId_base_common + 4055);             //  2009/05/27
        //public final static int 	CONST_qnmCfgId_uiSampleTimeInMs_org					=(  CONST_qnmCfgId_base_common  +  4056  );				//  2009/05/04
        //
        public final static short CONST_qnmCfgId_uiPts = (CONST_qnmCfgId_base_common + 4057);             //  2015/01/15
        public final static short CONST_qnmCfgId_ucbKeyFrame = (CONST_qnmCfgId_base_common + 4058);

        //
        public final static short CONST_qnmCfgId_tLastTime_showFrameInfo_020534 = (CONST_qnmCfgId_base_common + 4060);               //
        public final static short CONST_qnmCfgId_usCnt_pkts = (CONST_qnmCfgId_base_common + 4061);               //
        public final static short CONST_qnmCfgId_tSelectTime = (CONST_qnmCfgId_base_common + 4062);              //  2008/12/05
        public final static short CONST_qnmCfgId_usDiff_pkts = (CONST_qnmCfgId_base_common + 4063);             //  2009/04/18

        //
        public final static short CONST_qnmCfgId_ucbNeedKeyFrameFirst = (CONST_qnmCfgId_base_common + 4070);

        //
        public final static short CONST_qnmCfgId_usMaxSpeakers = (CONST_qnmCfgId_base_common + 4201);                //  2009/05/31
        public final static short CONST_qnmCfgId_usIndex_speaker = (CONST_qnmCfgId_base_common + 4202);             //  2013/07/18
        public final static short CONST_qnmCfgId_conf_iFourcc = (CONST_qnmCfgId_base_common + 4203);             //

        //
        public final static short CONST_qnmCfgId_usLayoutType = (CONST_qnmCfgId_base_common + 4205);             //  2010/12/27

        //
        public final static short CONST_qnmCfgId_iHkStatus = (CONST_qnmCfgId_base_common + 4214);
        public final static short CONST_qnmCfgId_bFlow512k_hg = (CONST_qnmCfgId_base_common + 4218);
        //
        public final static short CONST_qnmCfgId_conf_bitrateInKbps_dl  =  (  CONST_qnmCfgId_base_common  +  4219  );			//  2024/02/05
        public final  static short CONST_qnmCfgId_conf_bitrateInKbps_ul  =  (  CONST_qnmCfgId_base_common  +  4220  );			//  2024/02/05


        //  2014/08/15
        public final static short CONST_qnmCfgId_iWidth_pic_conf = (CONST_qnmCfgId_base_common + 4230);          //  2014/08/15
        public final static short CONST_qnmCfgId_iHeight_pic_conf = (CONST_qnmCfgId_base_common + 4231);         //  2014/08/15
        //
        //public final static int 		CONST_qnmCfgId_ucbShowBgWall_starter					=(  CONST_qnmCfgId_base_common  +  4232  );			//  2016/03/26
        //
        public final static short CONST_qnmCfgId_width = (CONST_qnmCfgId_base_common + 4233);            //  2017/06/12
        public final static short CONST_qnmCfgId_height = (CONST_qnmCfgId_base_common + 4234);           //  2017/06/12
        public final static short CONST_qnmCfgId_ucbConfCtrl_starter = (CONST_qnmCfgId_base_common + 4235);          //  2017/07/06


        //
        public final static short CONST_qnmCfgId_uiTranNo_avTask_starter = (CONST_qnmCfgId_base_common + 4250);          //  2014/11/22
        public final static short CONST_qnmCfgId_ucAvFlg = (CONST_qnmCfgId_base_common + 4251);          //  2015/03/12


        //
        public final static short CONST_qnmCfgId_startTime_task = (CONST_qnmCfgId_base_common + 4300);
        public final static short CONST_qnmCfgId_usMcuType = (CONST_qnmCfgId_base_common + 4301);
        //
        public final static short CONST_qnmCfgId_ca_sendData = (CONST_qnmCfgId_base_common + 4492);
        public final static short CONST_qnmCfgId_ca_usrName = (CONST_qnmCfgId_base_common + 4493);
        public final static short CONST_qnmCfgId_ca_tmp_passwd = (CONST_qnmCfgId_base_common + 4494);                //  2023/05/16
        //
        public final static short CONST_qnmCfgId_sxrz_servReply = (CONST_qnmCfgId_base_common + 4500);
        public final static short CONST_qnmCfgId_sxrz_cliData = (CONST_qnmCfgId_base_common + 4501);
        public final static short CONST_qnmCfgId_sHgCmd = (CONST_qnmCfgId_base_common + 4502);
        public final static short CONST_qnmCfgId_ca_random = (CONST_qnmCfgId_base_common + 4503);
        public final static short CONST_qnmCfgId_caGwCfg = (CONST_qnmCfgId_base_common + 4504);


        //
        public final static short CONST_qnmCfgId_usEnlargeType = (CONST_qnmCfgId_base_common + 4600);                //
        public final static short CONST_qnmCfgId_iChannel = (CONST_qnmCfgId_base_common + 4601);
        public final static short CONST_qnmCfgId_ip = (CONST_qnmCfgId_base_common + 4602);
        public final static short CONST_qnmCfgId_mouseStatus = (CONST_qnmCfgId_base_common + 4603);
        public final static short CONST_qnmCfgId_ucbOneBigLayout = (CONST_qnmCfgId_base_common + 4604);
        public final static short CONST_qnmCfgId_messengerId_oneBig = (CONST_qnmCfgId_base_common + 4605);
        public final static short CONST_qnmCfgId_ii = (CONST_qnmCfgId_base_common + 4606);
        public final static short CONST_qnmCfgId_curr_idinfo = (CONST_qnmCfgId_base_common + 4607);
        public final static short CONST_qnmCfgId_enlarge_x = (CONST_qnmCfgId_base_common + 4608);
        public final static short CONST_qnmCfgId_enlarge_y = (CONST_qnmCfgId_base_common + 4609);
        public final static short CONST_qnmCfgId_phone_usEnlargeType = (CONST_qnmCfgId_base_common + 4610);
        public final static short CONST_qnmCfgId_amplificationType = (CONST_qnmCfgId_base_common + 4611);
        public final static short CONST_qnmCfgId_channelName = (CONST_qnmCfgId_base_common + 4612);

        // 5000 - 5999
        public final static short CONST_qnmCfgId_uiVDevId = (CONST_qnmCfgId_base_common + 5000);             //  2008/01/25
        public final static short CONST_qnmCfgId_name = (CONST_qnmCfgId_base_common + 5001);             //  2009/08/25
        public final static short CONST_qnmCfgId_model = (CONST_qnmCfgId_base_common + 5002);                //  2008/01/25
        public final static short CONST_qnmCfgId_smsc = (CONST_qnmCfgId_base_common + 5003);             //  2008/01/25
        public final static short CONST_qnmCfgId_status = (CONST_qnmCfgId_base_common + 5004);               //  2008/01/25
        public final static short CONST_qnmCfgId_cusName = (CONST_qnmCfgId_base_common + 5005);              //  2008/01/25
        public final static short CONST_qnmCfgId_ucbDefaultSmServer = (CONST_qnmCfgId_base_common + 5006);               //  2008/01/25

        //
        public final static short CONST_qnmCfgId_wDevIdStr_lastRefreshed = (CONST_qnmCfgId_base_common + 5020);              //  2008/02/01

        //
        public final static short CONST_qnmCfgId_usRaType = (CONST_qnmCfgId_base_common + 5050);             //  2008/11/15
        public final static short CONST_qnmCfgId_ucButtonMask = (CONST_qnmCfgId_base_common + 5051);
        public final static short CONST_qnmCfgId_x = (CONST_qnmCfgId_base_common + 5052);                //  2008/11/12. x×ø±ê
        public final static short CONST_qnmCfgId_y = (CONST_qnmCfgId_base_common + 5053);                //  2008/11/12  y×ø±ê
        //  2016/12/26
        //public final static int 		CONST_qnmCfgId_uiEventId_unused							=(  CONST_qnmCfgId_base_common  +  2854  );				//  ÊÂ¼þµÄ±êÊ¶¡£
        //
        public final static short CONST_qnmCfgId_key = (CONST_qnmCfgId_base_common + 5056);              //  ¼üÖµ
        public final static short CONST_qnmCfgId_ucbDown = (CONST_qnmCfgId_base_common + 5057);              //  ÊÇ·ñ°´ÏÂ


        //
        public final static short CONST_qnmCfgId_uiTranNo_retrieveAll = (CONST_qnmCfgId_base_common + 5170);         //  2017/09/17
        public final static short CONST_qnmCfgId_usSeqNo_retrievePart = (CONST_qnmCfgId_base_common + 5171);         //  2017/09/17
        public final static short CONST_qnmCfgId_usSeqNo = (CONST_qnmCfgId_base_common + 5172);          //  2017/09/17


        // 6000 -
        //  public final static int 		CONST_qnmCfgId_										=(  =(  CONST_qnmCfgId_base_common  +  6000  );		//
        public final static short CONST_qnmCfgId_uiRuleType = (CONST_qnmCfgId_base_common + 6001);
        public final static short CONST_qnmCfgId_nCmd = (CONST_qnmCfgId_base_common + 6002);

        //  gps
        public final static short CONST_qnmCfgId_iLaInM10000f = (CONST_qnmCfgId_base_common + 6200);         //  4200. 2012/04/20
        public final static short CONST_qnmCfgId_iLoInM10000f = (CONST_qnmCfgId_base_common + 6201);         //

        //  ptz
        public final static short CONST_qnmCfgId_nAddr = (CONST_qnmCfgId_base_common + 6300);            //  4300, 2012/08/07
        public final static short CONST_qnmCfgId_nPelcoType = (CONST_qnmCfgId_base_common + 6301);           //


        //  2014/08/28
        public final static short CONST_qnmCfgId_usTaskProcObjType = (CONST_qnmCfgId_base_common + 6400);            //  4400. 2014/08/28


        //  2015/08/13
        public final static short CONST_qnmCfgId_ucbP2p = (CONST_qnmCfgId_base_common + 6504);               //  4504.  //  2015/08/05
        public final static short CONST_qnmCfgId_index_page = (CONST_qnmCfgId_base_common + 6505);
        public final static short CONST_qnmCfgId_max_usCnt = (CONST_qnmCfgId_base_common + 6506);                //  max of usCnt
        public final static short CONST_qnmCfgId_keyword = (CONST_qnmCfgId_base_common + 6507);              //


        //
        public final static short CONST_qnmCfgId_testSpeed_data = (CONST_qnmCfgId_base_common + 6620);   //  2016/12/01

        //
        public final static short CONST_qnmCfgId_mcuUsrName = (CONST_qnmCfgId_base_common + 6700);
        public final static short CONST_qnmCfgId_mcuPasswd = (CONST_qnmCfgId_base_common + 6701);
        public final static short CONST_qnmCfgId_mcuDomainName = (CONST_qnmCfgId_base_common + 6702);


        //  2008/05/15
        // 30000 - 31999
        public final static short CONST_qnmCfgId_debug_base = 30000;                                                 //  2015/09/01. 把dbg的定义放高点。以后就固定下来。

        //
        public final static short CONST_qnmCfgId_tTime_start = (CONST_qnmCfgId_debug_base + 1);
        public final static short CONST_qnmCfgId_usElapsed_toSend = (CONST_qnmCfgId_debug_base + 2);
        //  public final static int 		CONST_qnmCfgId_sElapsed_videoConferenceRecved		=(  CONST_qnmCfgId_debug_base  +  3  );

        public final static short CONST_qnmCfgId_tTime_serv_startToRecv = (CONST_qnmCfgId_debug_base + 20);
        public final static short CONST_qnmCfgId_usElapsed_serv_recved = (CONST_qnmCfgId_debug_base + 21);
        public final static short CONST_qnmCfgId_usElapsed_serv_startToProcess = (CONST_qnmCfgId_debug_base + 22);
        public final static short CONST_qnmCfgId_usElapsed_serv_toFindDst = (CONST_qnmCfgId_debug_base + 23);
        public final static short CONST_qnmCfgId_usElapsed_serv_toBeSwitched = (CONST_qnmCfgId_debug_base + 24);
        public final static short CONST_qnmCfgId_usElapsed_serv_toSend = (CONST_qnmCfgId_debug_base + 25);

        public final static short CONST_qnmCfgId_dwTickCnt_peer_recved = (CONST_qnmCfgId_debug_base + 40);
        public final static short CONST_qnmCfgId_usElapsed_peer_resp_toSend = (CONST_qnmCfgId_debug_base + 41);
        public final static short CONST_qnmCfgId_tTime_peer_recved = (CONST_qnmCfgId_debug_base + 42);

        public final static short CONST_qnmCfgId_tTime_serv_resp_startToRecv = (CONST_qnmCfgId_debug_base + 60);
        public final static short CONST_qnmCfgId_usElapsed_serv_resp_recved = (CONST_qnmCfgId_debug_base + 61);
        public final static short CONST_qnmCfgId_usElapsed_serv_resp_startToProcess = (CONST_qnmCfgId_debug_base + 62);
        public final static short CONST_qnmCfgId_usElapsed_serv_resp_toFindDst = (CONST_qnmCfgId_debug_base + 63);
        public final static short CONST_qnmCfgId_usElapsed_serv_resp_toBeSwitched = (CONST_qnmCfgId_debug_base + 64);
        public final static short CONST_qnmCfgId_usElapsed_serv_resp_toSend = (CONST_qnmCfgId_debug_base + 65);


        //
        //  #define		CONST_qnmCfgId_dwTickCnt_resp_recved				=(  CONST_qnmCfgId_debug_base  +  80  );
        //  #define		CONST_qnmCfgId_usElapsed_resp_end					=(  CONST_qnmCfgId_debug_base  +  81  );

        public final static short CONST_qnmCfgId_ucbGetTotal = (CONST_qnmCfgId_debug_base + 200);
        public final static short CONST_qnmCfgId_netStats_total = (CONST_qnmCfgId_debug_base + 201);
        public final static short CONST_qnmCfgId_qmdStatus_msgrMem = (CONST_qnmCfgId_debug_base + 202);
        public final static short CONST_qnmCfgId_qmdStatus_channelMem = (CONST_qnmCfgId_debug_base + 203);
        //
        public final static short CONST_qnmCfgId_showInfoFlgs = (CONST_qnmCfgId_debug_base + 210);   //  2015/09/01

        // 32000 -
        public final static short CONST_qnmCfgId_user = 32000;


        //
        public final static short CONST_qyCmd_getCfgs = (CONST_qyCmd_base + 2);
        //  2007/04/23, mis
        //public final static short CONST_qyCmd_showInfo = (CONST_qyCmd_base + 200);   //
        public final static short CONST_qyCmd_showInfo_small = (CONST_qyCmd_base + 201);   //
        public final static short CONST_qyCmd_showInfo_small_java = (CONST_qyCmd_base + 202);   //


        //
        public final static short CONST_qyCmd_refreshImObjListReq = (CONST_qyCmd_base + 205);    //  ??????????2007/05/06????????????imCommType???
        //
        public final static short CONST_qyCmd_refreshRecentFriendsReq = (CONST_qyCmd_base + 207);    //  ??????????2007/05/10
        //
        public final static short CONST_qyCmd_mcComm = (CONST_qyCmd_base + 210); //  2011/01/08
        //
        public final static short CONST_qyCmd_sendReq = (CONST_qyCmd_base + 220);

        //
        public final static short CONST_qyCmd_talkTo = (CONST_qyCmd_base + 300); //  ???2007/04/09

        public final static short CONST_qyCmd_sendTask = (CONST_qyCmd_base + 320);   //  ???????2007/06/23
        public final static short CONST_qyCmd_sendTaskReply = (CONST_qyCmd_base + 321);  //  ???????2007/06/23
        public final static short CONST_qyCmd_sendRobotTask = (CONST_qyCmd_base + 322);  //  ????????
        public final static short CONST_qyCmd_sendRobotTaskReply = (CONST_qyCmd_base + 323); //  ????????
        public final static short CONST_qyCmd_sendRobotTaskData = (CONST_qyCmd_base + 324);  //  ????????????????channel????????????????????????channel????, ????????channel???; ?????????????channel, ???channel????
        //
        public final static short CONST_qyCmd_sendMedia = (CONST_qyCmd_base + 330);  //  ??????
        //
        public final static short CONST_qyCmd_lastMsgInSession = (CONST_qyCmd_base + 340);   //  webMessenger?cgi????????????????????????daemon?????????????????????????????????????2007/12/11
        //
        public final static short CONST_qyCmd_sendVDevReq = (CONST_qyCmd_base + 350);    //  ??????????????????,2008/01/24


        //
        public final static short CONST_qnmMaxRegFields = 30;                    //  ÒÔÏÂµÄÉèÖÃÖµ²»ÄÜ³¬¹ý´ËÏî, Ö¸cols, »¹ÓÐ¼¸ÏîÈçmac0µÈ£¬²»°üº¬ÔÚÄÚ

        //
        //  ×¢²áÐÅÏ¢µÄ×Ö¶Î¶¨Òå
        public final static short CONST_fieldId_null = 0;
        public final static short CONST_fieldId_yhlx = 100;
        public final static short CONST_fieldId_syr = 200;
        public final static short CONST_fieldId_bm = 201;
        public final static short CONST_fieldId_dw = 202;
        public final static short CONST_fieldId_qtdw = 203;
        public final static short CONST_fieldId_bb = 204;
        public final static short CONST_fieldId_zb = 205;
        //
        public final static short CONST_fieldId_zcbh0 = 220;
        public final static short CONST_fieldId_zcbh1 = 221;
        public final static short CONST_fieldId_zcbh2 = 222;
        //  public final static int 		CONST_fieldId_zcsm0								223
        public final static short CONST_fieldId_zcsm1 = 224;
        public final static short CONST_fieldId_zcsm2 = 225;
        public final static short CONST_fieldId_jqlx = 240;
        public final static short CONST_fieldId_lxdh = 241;
        public final static short CONST_fieldId_sydd = 242;
        public final static short CONST_fieldId_zyyt = 243;
        public final static short CONST_fieldId_bz1 = 244;
        public final static short CONST_fieldId_bz2 = 245;

        /////////////////////
        ///
        public final static short CONST_qyEventType_is_base = 20000;         //  2007/05/08, mis
        //
        public final static short CONST_qyEventType_im_newMessenger = (CONST_qyEventType_is_base + 100);
        //
        public final static short CONST_qyEventType_im_messengerLogon = (CONST_qyEventType_is_base + 110);
        public final static short CONST_qyEventType_im_messengerLogoff = (CONST_qyEventType_is_base + 111);
        //
        public final static short CONST_qyEventType_im_connErr = (CONST_qyEventType_is_base + 115);  //
        public final static short CONST_qyEventType_im_memoryErr = (CONST_qyEventType_is_base + 116);    //  2011/02/05

        //
        //  ÒÔÏÂÍ¨ÐÅµÄÊý¾Ý°ü¸ñÊ½¡£

        public final static short CONST_qnmAuthCmd_startSession = 0;                 //  ÊÜ¿Ø¶Ë£¨»òÆäËû£©Á¬½Ó·þÎñÆ÷£¬ÒÔÆô¶¯Ò»¸öÕý³£µÄ»á»°
        public final static short CONST_qnmAuthCmd_getHint = 1;                  //  ×¢²á½çÃæÁ¬½Ó·þÎñÆ÷£¬ÒÔÈ¡µÃÌáÊ¾ÐÅÏ¢£¬ÏÞÓÃÔÚnetMc2
        public final static short CONST_qnmAuthCmd_startProxySession = 2;                    //  ·þÎñÆ÷Á¬½Ó´úÀí£¬ÒÔÆô¶¯´úÀí»á»°
        public final static short CONST_qnmAuthCmd_guiTalk = 3;                  //  ½çÃæÁ¬½ÓDaemon, ÒÔÍê³ÉÄ³Ð©Ö¸Áî
        public final static short CONST_qnmAuthCmd_cgiTalk = 4;                  //  cgiÁª½ÓDaemon, ÒÔÍê³ÉwebÏà¹ØµÄ¹¤×÷£¬2007/11/21
        public final static short CONST_qnmAuthCmd_clientHello = 5;                    //  2008/06/09


    }


    public static class QY_CFGITEM {
        public short cfgId;
        public short dataType;
        public int len;
    }

    public static class QY_CFGITEM_L32 extends QY_CFGITEM {
        public int ulVal;
    }


    public static class QY_CFGITEM_L64 extends QY_CFGITEM {           //  ÒòÎª´Ó×Ö½ÚË³ÐòÉÏ£¬¿ÉÄÜ__int64ºÍunsigned  int²»ÄÜË³×Ö½Ú´æ·Å£¬¹ÊÖ»ÄÜÓÃÒ»¸öchar»º´æÀ´Ìæ»»

        public long ulVal;   //  unsigned  __int64								ui64Val;
    }

    public static class QY_CFGITEM_BYTE extends QY_CFGITEM {           //  2007/08/05

        public byte ucVal;
    }


    public static class QY_CFGITEM_SHORT extends QY_CFGITEM {           //  2007/08/05
        public short usVal;

    }

    public static class QY_CFGITEM_STR extends QY_CFGITEM {
        public short len;
        public String str;
    }


    public static class QY_CFGITEM_DATA extends QY_CFGITEM {
        public short len;
        public ByteBuffer buf;//[bufSize_cfgItemData + 1];
    }


    public static class QY_CFGITEM_LDATA extends QY_CFGITEM {
        public int ulLen;
        public ByteBuffer p;
    }

    public static class QNM_CNT_CFG {
        public boolean ucbAutoCntIp;     //  2006/06/29
        public byte[] cntIp = new byte[CONST_qyMaxIpLen + 1];//[]; 默认服务器ip地址
        public boolean ucbSys_cntIp;                         //  ÊÇ·ñhkey_local_machineÀïµÄ¡£2009/01/16
        public byte[] cntAddr1 = new byte[CONST_maxCntAddrLen + 1];  // 服务器地址1
        public byte[] cntAddr2 = new byte[CONST_maxCntAddrLen + 1]; //服务器地址2
        public boolean ucbSys_cntAddrs;//[2];                      //  ÊÇ·ñhkey_local_machineÀïµÄ¡£2009/01/16
        public int usCntPort;                                       //服务器端口号
        public boolean ucbSys_cntPort;                           //  hkey_local_machine;  2010/09/24
        public String usr;//[256];                             //  2019/04/08
        public String passwd;//[256];                          //  2019/04/08
        public boolean ucbNoSavePasswd;                      //  2019/04/08
        public boolean ucbNoAutoLogon;                           //  2019/04/08
    }

    public static class QWM_DYN_CFG {                   //  2006/07/01
        public QNM_CNT_CFG cnt = new QNM_CNT_CFG();
    }

    public static class QY_ENV {
        public int iEnvType;
        public int iPlatformId;
        public short usLangId;                            //  ²Ù×÷ÏµÍ³ÓïÑÔÀàÐÍ£¬2005/11/03
        //
        public boolean bDebugInfo;                            //  2008/11/19. ÊÇ·ñÏÔÊ¾Ò»Ð©debugÐÅÏ¢£¬¶ÔÔÚrelease°æ±¾ÏÂÏÔÊ¾Ä³Ð©ÐÅÏ¢ÓÐÓÃ


    }


    public static class QNM_COMM_AUTHINFO {

        public ByteBuffer ver = ByteBuffer.allocateDirect(CONST_qyMaxVerLen);//[];
        public ByteBuffer ip = ByteBuffer.allocateDirect(CONST_qyMaxIpLen);//[];
        public byte cmd;               //  (1.4)°æ±¾Ç°Óëº¬(1.4)°æ±¾ºóÖ®¼äµÄÖ÷ÒªÊý¾Ý½á¹¹ÉÏµÄÇø±ð
        public int uiQwmType;         //  (0208)ºóÖ§³Ö


        public byte[] toByteArray() {
            int verSize = ver.remaining();
            int ipSize = ip.remaining();
            int bufferSize = verSize + ipSize + Byte.BYTES + Integer.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.put(ver);
            buffer.put(ip);
            buffer.put(cmd);
            buffer.putInt(uiQwmType);
            return buffer.array();
        }
    }


    public static class qyFuncs {


        public static byte[] myWChar2Utf8(String wStr, byte[] buf, int bufSize) {
            int iErr = -1;
            int bufSizeNeeded = 0;
            try {
                safeStrnCpy(wStr, buf, bufSize);
                iErr = 0;
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }

            // Return the buffer if successful, otherwise return null
            return (iErr != 0) ? null : buf;
        }

        //
        public static String myUtf82WChar(byte[] pBuf, String wBuf, int cntof_wBuf) {
            return myUtf82WChar(mytoString(pBuf), wBuf, cntof_wBuf);
        }

        public static String myUtf82WChar(String str, String wBuf, int cntof_wBuf) {
            int iErr = -1;

            try {
                // Convert the UTF-8 String to wide characters
                byte[] utf8Bytes = str.getBytes(StandardCharsets.UTF_8);
                String wstr = new String(utf8Bytes, StandardCharsets.UTF_8);

                // Copy the wide characters to the wBuf with tmpFunc.qyFuncs.mytcsncpy function
                wBuf = wstr;//, cntof_wBuf);

                iErr = 0;
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }

            // Return wBuf if successful, otherwise return null
            return (iErr != 0) ? null : wBuf;
        }


        public static qnmCommProc.QY_CFGITEM qyntohCfgItem(ByteBuffer p) {
            int len = 0;
            int headLen = 4;

            if (p.capacity() < headLen) {
                return null;
            }

            short cfgId = p.getShort();
            short dataType = p.getShort();
            qnmCommProc.QY_CFGITEM pItem;
            switch (dataType) {
                case CONST_qyDataType_long: {
                    pItem = new QY_CFGITEM_L32();
                    ((QY_CFGITEM_L32)pItem).ulVal = p.getInt();
                    len = Integer.BYTES;
                }
                break;
                case CONST_qyDataType_char: {
                    pItem = new QY_CFGITEM_BYTE();
                    ((QY_CFGITEM_BYTE)pItem).ucVal = p.get();
                    len = Byte.BYTES;
                }
                break;
                case CONST_qyDataType_short: {
                    pItem = new QY_CFGITEM_SHORT();
                    ((QY_CFGITEM_SHORT)pItem).usVal = p.getShort();
                    len = Short.BYTES;
                }
                break;
                case CONST_qyDataType_l64: {
                    pItem = new QY_CFGITEM_L64();
                    ((QY_CFGITEM_L64)pItem).ulVal = p.getLong();
                    len = Long.BYTES;
                }
                break;
                case CONST_qyDataType_data:{
                    pItem = new QY_CFGITEM_DATA();
                    short data_len = p.getShort();
                    byte[] data = new byte[data_len];
                    p.get(data, 0, data_len);
                    ((QY_CFGITEM_DATA)pItem).buf = ByteBuffer.wrap(data);
                    len = Short.BYTES + data_len;
                    break;
                }
                case CONST_qyDataType_str:
                case CONST_qyDataType_utf8Str: {
                    pItem = new QY_CFGITEM_STR();
                    short str_len = p.getShort();
                    byte[] buf = new byte[str_len];
                    p.get(buf, 0, str_len);
                    ((QY_CFGITEM_STR) pItem).str = new String(buf, StandardCharsets.UTF_8);
                    len = Short.BYTES + str_len;
                }
                break;
                case CONST_qyDataType_lData: {
                    pItem = new QY_CFGITEM_LDATA();
                    int ulLen = p.getInt();
                    byte[] data = new byte[ulLen];
                    p.get(data, 0, ulLen);
                    ((QY_CFGITEM_LDATA)pItem).ulLen = ulLen;
                    ((QY_CFGITEM_LDATA)pItem).p = ByteBuffer.wrap(data);
                    len = Integer.BYTES + ulLen;
                }
                break;
                default:
                    Log.e("unknown dataType:"+dataType);
                    return  null;
            }
            pItem.cfgId = cfgId;
            pItem.dataType = dataType;
            pItem.len = len + headLen;
            return pItem;
        }

        public static String getIpFromName() {
            return null;
        }


        public static int getQyEnv(byte[] pDynLibs, QY_ENV pEnv) {
            pEnv.usLangId = CONST_langId_PRC;

            return 0;
        }

        //
        public static qyTypes.QY_DMITEM[] CONST_fieldIdTable_en = new qyTypes.QY_DMITEM[]

                {
                        new qyTypes.QY_DMITEM(CONST_fieldId_yhlx, ("用户类型")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_syr, ("Username")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_bm, ("Department")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_dw, ("Company")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_bb, ("Local branch")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_zb, ("Group")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_qtdw, ("Other")),
                        //
                        new qyTypes.QY_DMITEM(CONST_fieldId_zcbh0, ("Asset no")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_zcbh1, ("Asset no1")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_zcbh2, ("Asset no2")),
                        //  #define		CONST_fieldId_zcsm0								123
                        new qyTypes.QY_DMITEM(CONST_fieldId_zcsm1, ("Asset note1")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_zcsm2, ("Asset note2")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_jqlx, ("Type")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_lxdh, ("Telephone")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_sydd, ("Address")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_zyyt, ("Use for")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_bz1, ("Remarks1")),
                        new qyTypes.QY_DMITEM(CONST_fieldId_bz2, ("Remarks2")),
                        //
                        new qyTypes.QY_DMITEM(-1, ""),

                };

        //
        public static int makeQwmAuthInfo(byte[] ver, qyObjs.QWM_MACS_INFO pMacsInfo, QNM_COMM_AUTHINFO pAuthInfo) {
            int i = 0;

            if (null == pAuthInfo) return -1;

            if (null != ver)
                //qyMemoryFunc.qyFuncs.mymemcpy(pAuthInfo.ver.array(), ver, CONST_qyMaxVerLen);
                if (null != pMacsInfo) {

                }
            return 0;
        }


        public static int qnmOpenSession(String servIp, int usPort, qyObjs.QWM_MACS_INFO pMacsInfo, byte ucCmd, int iServiceId, byte[] ver,
                                         Object pCommEncCtx, qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_PARAM pSockParam,
                                         byte[] data, int piDataSize, qyComm.QY_COMM_HEAD tmp_head,  ByteBuffer tmp_bb) {
            int iErr = -1;

            //
            qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(0,0);
            if  (  tmpVar ==  null  )  {
                Log.e("qy","qnmOpenSession failed, tmpVar is null");
                return  -1;
            }
            qyShowInfoCommProc.Ctx_thread_showInfo ctxThreadShowInfo=tmpVar.m_ctxThreadShowInfo;
            if  (  ctxThreadShowInfo==null) {
                return  -1;
            }



            //char ip[16];
            qnmCommProc_mis.qyFuncs.TMP_ip ip = new qnmCommProc_mis.qyFuncs.TMP_ip();
            QNM_COMM_AUTHINFO authInfo = new QNM_COMM_AUTHINFO();
            qyComm.QY_COMM_RESP tmpResp = new qyComm.QY_COMM_RESP();
            //char tmpBuf[4096];
            qyMcMainDaemon_isCli.qyFuncs.TMP_buf4096 tmpBuf = ctxThreadShowInfo.tmpBuf_buf4096;//new qyMcMainDaemon_isCli.qyFuncs.TMP_buf4096();// todo qy
            int sizeof_tmpBuf = bufSize_4096;
            String str;

            //
            if (0 == ucCmd) {
                if (null == pMacsInfo || 0 == pMacsInfo.nMacs) return -1;
            }

            if (!guiShare.GuiShare.pf_bQySockOpen.call(pSock)) {       //  Èç¹ûÇ°ÃæÒÑ½¨Á¢Á¬½Ó£¬Ôò²»ÐèÒª½¨Á¢ÐÂÁ¬½Ó
                if (0 != guiShare.GuiShare.pf_qyConnect.call(servIp, usPort, pSockParam, pSock)) {

                    //if (CONST_Status_Window_Function_Open == 0)
                    if (false)
                    {
                        str = String.format("qnmOpenSession: qyConnect %s failed", servIp);
                        Log.d("qy", str);
                    }
                    if (guiShare.GuiShare.pf_bQySockOpen.call(pSock))
                        guiShare.GuiShare.pf_qyDisconnect.call(pSock);
                    return iErr;
                }
            }
//            if (0 != getLocalIpBySocket(pSock, ip.ip)) return iErr;
            //mymemset((byte[])authInfo, 0, sizeof(QNM_COMM_AUTHINFO));
            if (0 != makeQwmAuthInfo(ver, pMacsInfo, authInfo)){
                if (guiShare.GuiShare.pf_bQySockOpen.call(pSock))
                    guiShare.GuiShare.pf_qyDisconnect.call(pSock);
                return iErr;
            }
            //qyMemoryFunc.qyFuncs.mymemcpy(authInfo.ip.array(), ip.ip.array(), CONST_qyMaxIpLen);
            authInfo.cmd = ucCmd;
            String ip_str = "";
            for (int i = 0; i < CONST_qyMaxIpLen; i++) {
                ip_str += authInfo.ip.array()[i] + ".";
            }
            Log.e("ip_str:" + ip_str);
            byte[] buf = authInfo.toByteArray();

            if (0 != qyTellService((int) iServiceId, ver, pCommEncCtx, buf, buf.length, pSock, pSockParam.to, pSession,tmp_head,  tmp_bb))
            {
                if (guiShare.GuiShare.pf_bQySockOpen.call(pSock))
                    guiShare.GuiShare.pf_qyDisconnect.call(pSock);
                return iErr;
            }
            if (0 != qyRecvResp(pSession, pSock, pSockParam.to, tmpResp, tmpBuf.buf, (int) sizeof_tmpBuf,tmp_bb))
            {
                if (guiShare.GuiShare.pf_bQySockOpen.call(pSock))
                    guiShare.GuiShare.pf_qyDisconnect.call(pSock);
                return iErr;
            }
            //  Log.e(TAG,  "qwmOpenSession: get respCode of qyTellService is %s",  qyGetDesByType1(  CONST_qyRcTable,  tmpResp.head.usCode  )  );
            //  ÕâÀï²»ÄÜÓÃÀ´ÅÐ¶Ï·µ»ØÂë£¬ÒòÎªÊÇÍ¨ÓÃµÄ½Ó¿Ú£¬Ó¦ÔÚÆäËûµØ·½ÅÐ¶Ï£¬2005/06/25
            //  if  (  !isRcOk(  tmpResp.head.usCode  )  )  goto  errLabel; 

            iErr = 0;


            if (0 != iErr) {
                if (guiShare.GuiShare.pf_bQySockOpen.call(pSock))
                    guiShare.GuiShare.pf_qyDisconnect.call(pSock);
            } else {
                if (0 != piDataSize && null != data) {
                    piDataSize = Math.min(piDataSize, tmpResp.head.len);
                    //qyMemoryFunc.qyFuncs.mymemcpy(data, tmpBuf.buf.array(), piDataSize);
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    buffer.put(tmpBuf.buf.array(),0,piDataSize);
                }
            }

            return iErr;
        }


        //
        public static void qnmCloseSession(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo) {
            qyComm.QY_COMM_RESP tmpResp = new qyComm.QY_COMM_RESP();

            if (guiShare.GuiShare.pf_bQySockOpen.call(pSock)) {
                if (isRcOk(pSession.usLastRespCode_i) || pSession.usLastRespCode_i == CONST_qyRc_user) {
                    /*
                    if (0 == qySendReq(pSession, pSock, pTo, CONST_qyCmd_end, null, 0)) {
                        qyRecvResp(pSession, pSock, pTo, tmpResp, null, 0);
                    }

                     */
                }
                //
                guiShare.GuiShare.pf_qyDisconnect.call(pSock);
            }
            return;
        }

        //
        public static int qyRecvResp(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, qyComm.QY_COMM_RESP pResp, ByteBuffer dataBuf, int bufSize,ByteBuffer tmp_bb) {
            int iErr = -1;
            byte[] encBuf = null;
            int encLen = CONST_maxReqDataLen_enc_netMc;
//            byte[] buf = new qyComm.QY_COMM_HEAD().toArrayBuffer();
            //byte[] resp_buf = pResp.toByteArray(tmp_bb);
            ByteBuffer head = tmp_bb;//ByteBuffer.allocate(8);
            if (0 > qyRecvEx(pSock, head, 8, pTo.iRead)) return iErr;
            head.flip();
            pResp.head.usCode = head.getShort();
            pResp.head.ucFlg = head.get();
            pResp.head.reserved = head.get();
            pResp.head.len = head.getInt();

//            pResp.head.len = qyntohi(pResp.head.len);

            //if (null == (encBuf = new byte[encLen])) return iErr;

            if (pResp.head.len > encLen) return iErr;
            ByteBuffer data = tmp_bb;//ByteBuffer.wrap(encBuf);
//            data.limit(pResp.head.len);
            if (0 != pResp.head.len) {
                if (0 > qyRecvEx(pSock, data, pResp.head.len,  pTo.iRead))
                    return iErr;

                if (pSession.service.encType == CONST_qyEncType_qwm) {

                } else if (pResp.head.len > bufSize) {
                    iErr = -2;
                    return iErr;
                } else {
                    //qyMemoryFunc.qyFuncs.mymemcpy(dataBuf, encBuf, pResp.head.len);
                }
            }


            pResp.head.usCode = qyntohs((short) pResp.head.usCode);
            pSession.usLastRespCode_i = pResp.head.usCode;

            if (pResp.head.len != 0) {
                data.flip();
                dataBuf.put(data.array());
            }

            iErr = 0;


//            if (null != encBuf) qyMemoryFunc.qyFuncs.myfree( encBuf);

            return iErr;

        }

        //
        public interface PF_bServConnected {
            boolean call(QY_ENV pEnv, String serverIp, int serverPort, Object pParam, int serviceId, String ver, qyEncFunc.QY_ENC_CTX pCommEncCtx, commonSock.SOCK_PARAM pSockParam);
        }


        //取得正确的服务器地址信息
        public static int getCorrectCnt(QY_ENV pEnv, QWM_DYN_CFG pDynCfg, PF_bServConnected pf_bServConnected, Object pParam, int serviceId, String ver, qyEncFunc.QY_ENC_CTX pCommEncCtx, commonSock.SOCK_PARAM pSockParam, qyObjs.QY_LOGICAL_SERVER pCnt) {
            int iErr = -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = (qmCommFunc_mis.MIS_CNT) pParam;
            if (!pf_bServConnected.call(pEnv, pMisCnt.server.ip, pDynCfg.cnt.usCntPort, pParam, serviceId, ver, pCommEncCtx, pSockParam))
                return iErr;

            if (pCnt != null) {
                pCnt.ip = pMisCnt.server.ip;
                pCnt.port = pDynCfg.cnt.usCntPort;
            }

            iErr = 0;
            return iErr;
        }
    }
}
