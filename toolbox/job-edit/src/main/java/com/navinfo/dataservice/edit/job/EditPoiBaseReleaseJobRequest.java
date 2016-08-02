package com.navinfo.dataservice.edit.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import com.navinfo.dataservice.jobframework.exception.JobCreateException;
import com.navinfo.dataservice.jobframework.exception.JobException;
import com.navinfo.dataservice.jobframework.runjob.AbstractJobRequest;
import com.navinfo.dataservice.jobframework.runjob.JobCreateStrategy;

public class EditPoiBaseReleaseJobRequest extends AbstractJobRequest {
	private List<Integer> gridIds;
	private int targetDbId;
	private List<String> checkRules;
	private List<String> batchRules;


	@Override
	public String getJobType() {
		return "editPoiBaseRelease";
	}

	@Override
	public void validate() throws JobException {
		// TODO Auto-generated method stub
		
	}

	public List<Integer> getGridIds() {
		return gridIds;
	}

	public void setGridIds(List<Integer> gridIds) {
		this.gridIds = gridIds;
	}

	public int getTargetDbId() {
		return targetDbId;
	}

	public void setTargetDbId(int targetDbId) {
		this.targetDbId = targetDbId;
	}

	public List<String> getCheckRules() {
		return checkRules;
	}

	public void setCheckRules(List<String> checkRules) {
		this.checkRules = checkRules;
	}

	public List<String> getBatchRules() {
		return batchRules;
	}

	public void setBatchRules(List<String> batchRules) {
		this.batchRules = batchRules;
	}

	@Override
	public void defineSubJobRequests() throws JobCreateException {
		subJobRequests = new HashMap<String,AbstractJobRequest>();
		//validation
		List<String> checkRule=new ArrayList<String>();
//		String rulestr = "GLM01003,GLM01004,GLM01005,GLM01006,GLM01007,GLM01008,GLM01010,GLM01012,GLM01013,GLM01014,GLM01015,GLM01017,GLM01018,GLM01020,GLM01021,GLM01023,GLM01025,GLM01026,GLM01027,GLM01028,GLM01031,GLM01032,GLM01033,GLM01038,GLM01039,GLM01040,GLM01041,GLM01042,GLM01043,GLM01064,GLM01065,GLM01066,GLM01067,GLM01068,GLM01069,GLM01070,GLM01081,GLM01082,GLM01083,GLM01084,GLM01085,GLM01086,GLM01087,GLM01088,GLM01089,GLM01090,GLM01091,GLM01092,GLM01093,GLM01094,GLM01095,GLM01096,GLM01097,GLM01098,GLM01099,GLM01100,GLM01101,GLM01102,GLM01103,GLM01104,GLM01105,GLM01106,GLM01108,GLM01109,GLM01110,GLM01112,GLM01113,GLM01114,GLM01115,GLM01116,GLM01117,GLM01118,GLM01119,GLM01120,GLM01121,GLM01122,GLM01123,GLM01125,GLM01126,GLM01127,GLM01128,GLM01129,GLM01130,GLM01131,GLM01132,GLM01133,GLM01134,GLM01135,GLM01136,GLM01137,GLM01138,GLM01139,GLM01140,GLM01142,GLM01143,GLM01144,GLM01145,GLM01146,GLM01147,GLM01148,GLM01149,GLM01150,GLM01151,GLM01152,GLM01154,GLM01155,GLM01156,GLM01157,GLM01159,GLM01160,GLM01166,GLM01167,GLM01169,GLM01170,GLM01173,GLM01174,GLM01175,GLM01176,GLM01178,GLM01179,GLM01180,GLM01181,GLM01183,GLM01184,GLM01185,GLM01186,GLM01187,GLM01188,GLM01189,GLM01190,GLM01191,GLM01192,GLM01193,GLM01194,GLM01195,GLM01196,GLM01197,GLM01198,GLM01199,GLM01201,GLM01203,GLM01204,GLM01205,GLM01206,GLM01207,GLM01208,GLM01209,GLM01210,GLM01211,GLM01212,GLM01213,GLM01214,GLM01221,GLM01230,GLM01231,GLM01232,GLM01234,GLM01235,GLM01236,GLM01237,GLM01238,GLM01239,GLM01240,GLM01241,GLM01242,GLM01243,GLM01244,GLM01245,GLM01246,GLM01247,GLM01248,GLM01249,GLM01250,GLM01251,GLM01252,GLM01254,GLM01255,GLM01256,GLM01257,GLM01258,GLM01259,GLM01260,GLM01261,GLM01262,GLM01263,GLM01264,GLM01265,GLM01266,GLM01267,GLM01268,GLM01269,GLM01270,GLM01272,GLM01275,GLM01276,GLM01277,GLM01278,GLM01279,GLM01280,GLM01281,GLM01282,GLM01283,GLM01284,GLM01287,GLM01288,GLM01290,GLM01291,GLM01295,GLM01296,GLM01297,GLM01306,GLM01307,GLM01340,GLM01343,GLM01348,GLM01349,GLM01353,GLM01356,GLM01357,GLM01358,GLM01359,GLM01362,GLM01363,GLM01364,GLM01365,GLM01366,GLM01367,GLM01368,GLM01369,GLM01372,GLM01375,GLM01376,GLM01377,GLM01378,GLM01379,GLM01380,GLM01381,GLM01382,GLM01383,GLM01384,GLM01385,GLM01386,GLM01387,GLM01388,GLM01389,GLM01390,GLM01391,GLM01392,GLM01393,GLM01394,GLM01395,GLM01396,GLM01398,GLM01399,GLM01400,GLM01401,GLM01403,GLM01404,GLM01405,GLM01406,GLM01407,GLM01411,GLM01413,GLM01416,GLM01417,GLM01418,GLM01419,GLM01430,GLM01431,GLM01432,GLM01437,GLM01438,GLM01439,GLM01441,GLM01442,GLM01444,GLM01445,GLM01446,GLM01447,GLM01448,GLM01449,GLM01450,GLM01451,GLM01454,GLM01455,GLM01456,GLM01457,GLM01458,GLM01459,GLM01460,GLM01461,GLM01462,GLM01463,GLM01464,GLM01465,GLM01467,GLM01468,GLM01469,GLM01470,GLM01471,GLM01480,GLM01482,GLM01483,GLM01505,GLM01513,GLM01514,GLM01515,GLM01516,GLM01517,GLM01519,GLM01520,GLM01521,GLM01522,GLM01524,GLM01525,GLM01526,GLM01527,GLM01528,GLM01529,GLM01530,GLM01531,GLM01532,GLM01533,GLM01534,GLM01536,GLM01539,GLM01540,GLM01541,GLM01542,GLM01543,GLM01544,GLM01546,GLM01547,GLM01548,GLM01549,GLM01557,GLM01565,GLM01566,GLM01567,GLM01574,GLM01575,GLM01576,GLM01577,GLM01578,GLM01579,GLM01582,GLM01583,GLM02004,GLM02007,GLM02008,GLM02011,GLM02012,GLM02013,GLM02015,GLM02019,GLM02021,GLM02038,GLM02053,GLM02054,GLM02056,GLM02058,GLM02062,GLM02066,GLM02074,GLM02075,GLM02076,GLM02078,GLM02079,GLM02080,GLM02084,GLM02085,GLM02086,GLM02087,GLM02088,GLM02089,GLM02090,GLM02091,GLM02093,GLM02094,GLM02095,GLM02096,GLM02099,GLM02115,GLM02116,GLM02117,GLM02118,GLM02129,GLM02130,GLM02131,GLM02132,GLM02154,GLM02202,GLM02205,GLM02206,GLM02208,GLM02209,GLM02210,GLM02211,GLM02213,GLM02218,GLM02219,GLM02220,GLM02230,GLM02231,GLM02232,GLM02233,GLM02234,GLM02235,GLM02236,GLM02237,GLM02238,GLM02239,GLM02240,GLM02243,GLM02244,GLM02245,GLM02248,GLM02251,GLM02255,GLM02256,GLM02257,GLM02258,GLM02259,GLM02263,GLM02264,GLM02265,GLM02271,GLM02999,GLM03001,GLM03003,GLM03038,GLM03039,GLM03041,GLM03042,GLM03045,GLM03046,GLM03047,GLM03048,GLM03050,GLM03053,GLM03054,GLM03055,GLM03056,GLM03058,GLM03059,GLM03061,GLM03062,GLM03064,GLM03065,GLM03066,GLM03067,GLM03068,GLM03069,GLM03070,GLM03071,GLM03072,GLM03073,GLM03077,GLM03078,GLM03079,GLM03080,GLM03081,GLM03082,GLM03083,GLM03084,GLM03085,GLM03086,GLM03087,GLM03089,GLM03090,GLM03091,GLM03092,GLM03093,GLM03094,GLM03097,GLM03999,GLM04002,GLM04004,GLM05001,GLM05002,GLM05003,GLM05004,GLM05008,GLM05009,GLM05010,GLM05011,GLM05012,GLM05013,GLM05015,GLM05017,GLM05018,GLM05019,GLM05020,GLM05021,GLM05022,GLM05023,GLM05024,GLM05025,GLM05026,GLM05027,GLM05028,GLM05029,GLM05031,GLM05032,GLM05033,GLM05035,GLM05036,GLM05037,GLM05041,GLM05042,GLM05043,GLM05044,GLM05046,GLM05048,GLM05049,GLM05050,GLM05053,GLM05054,GLM05055,GLM05056,GLM05070,GLM05071,GLM05072,GLM05073,GLM05074,GLM05075,GLM05076,GLM05077,GLM05079,GLM05080,GLM05082,GLM05083,GLM05084,GLM05085,GLM05086,GLM05087,GLM05088,GLM05089,GLM05090,GLM05094,GLM05095,GLM05097,GLM05098,GLM05101,GLM05102,GLM05104,GLM05105,GLM05106,GLM05107,GLM05108,GLM06005,GLM06006,GLM07001,GLM07003,GLM07004,GLM07005,GLM07006,GLM07007,GLM08001,GLM08002,GLM08003,GLM08004,GLM08005,GLM08006,GLM08009,GLM08010,GLM08011,GLM08012,GLM08013,GLM08014,GLM08015,GLM08016,GLM08017,GLM08022,GLM08023,GLM08024,GLM08025,GLM08028,GLM08030,GLM08031,GLM08032,GLM08033,GLM08034,GLM08035,GLM08036,GLM08037,GLM08038,GLM08039,GLM08040,GLM08041,GLM08042,GLM08043,GLM08044,GLM08046,GLM08047,GLM08048,GLM08049,GLM08050,GLM08051,GLM08052,GLM08054,GLM09003,GLM09004,GLM09005,GLM09006,GLM09007,GLM09009,GLM09010,GLM09011,GLM09012,GLM09013,GLM09014,GLM09015,GLM09016,GLM09017,GLM09018,GLM09019,GLM09021,GLM10001,GLM10003,GLM10006,GLM10007,GLM10008,GLM10009,GLM10012,GLM10014,GLM10016,GLM10017,GLM10018,GLM11002,GLM11003,GLM11004,GLM11005,GLM11006,GLM11007,GLM11008,GLM11015,GLM11016,GLM11024,GLM11025,GLM11026,GLM11027,GLM11028,GLM11029,GLM11030,GLM11034,GLM11036,GLM11037,GLM11038,GLM11039,GLM11040,GLM11041,GLM11048,GLM11049,GLM11059,GLM11060,GLM11071,GLM11072,GLM11073,GLM11074,GLM11075,GLM11076,GLM11077,GLM11079,GLM11088,GLM11090,GLM11091,GLM11094,GLM11095,GLM11098,GLM11100,GLM11101,GLM11102,GLM11103,GLM11104,GLM11105,GLM11106,GLM11107,GLM11110,GLM11111,GLM11112,GLM11114,GLM11115,GLM11116,GLM11118,GLM11119,GLM11120,GLM11122,GLM11124,GLM12001,GLM12002,GLM12003,GLM12004,GLM12005,GLM12006,GLM12007,GLM12009,GLM12010,GLM12011,GLM12012,GLM12014,GLM12016,GLM12017,GLM12018,GLM12019,GLM12020,GLM12021,GLM12022,GLM12024,GLM12025,GLM12026,GLM12027,GLM12028,GLM12029,GLM12030,GLM12031,GLM12032,GLM12033,GLM12036,GLM12037,GLM12038,GLM12040,GLM12042,GLM12043,GLM12044,GLM12045,GLM12047,GLM12049,GLM12051,GLM12052,GLM12053,GLM12054,GLM13002,GLM13003,GLM14003,GLM14006,GLM14010,GLM15001,GLM15003,GLM15004,GLM15005,GLM15006,GLM15007,GLM15008,GLM15009,GLM15010,GLM15012,GLM15013,GLM16008,GLM17007,GLM17012,GLM17013,GLM18001,GLM18002,GLM18008,GLM18015,GLM19001,GLM19006,GLM19009,GLM19013,GLM19014,GLM19021,GLM19022,GLM19023,GLM20001,GLM20002,GLM20004,GLM20005,GLM20008,GLM20009,GLM20010,GLM20014,GLM20034,GLM20035,GLM21002,GLM21007,GLM21008,GLM21012,GLM22006,GLM22010,GLM23001,GLM23002,GLM23003,GLM23004,GLM23005,GLM23006,GLM23007,GLM23009,GLM23010,GLM23011,GLM26002,GLM26003,GLM26004,GLM26005,GLM26006,GLM26007,GLM26008,GLM26009,GLM26010,GLM26011,GLM26012,GLM26013,GLM26014,GLM26017,GLM26032,GLM26033,GLM26039,GLM26040,GLM26041,GLM26042,GLM26043,GLM26044,GLM26045,GLM26046,GLM26047,GLM26048,GLM26049,GLM26050,GLM26051,GLM26052,GLM26999,GLM28001,GLM28002,GLM28010,GLM28019,GLM28021,GLM28024,GLM28025,GLM28026,GLM28028,GLM28029,GLM28030,GLM28031,GLM28032,GLM28033,GLM28035,GLM28036,GLM28039,GLM28043,GLM28050,GLM28062,GLM28063,GLM28065,GLM28066,GLM28067,GLM29003,GLM29007,GLM29008,GLM29010,GLM29011,GLM29012,GLM29013,GLM29014,GLM29016,GLM29017,GLM29030,GLM29032,GLM29047,GLM29048,GLM29049,GLM29050,GLM29056,GLM29060,GLM29061,GLM29062,GLM29078,GLM29079,GLM29082,GLM29094,GLM29095,GLM29099,GLM29102,GLM29103,GLM29104,GLM29111,GLM29114,GLM29115,GLM29120,GLM29121,GLM29123,GLM29127,GLM29128,GLM29132,GLM29136,GLM29137,GLM29138,GLM29139,GLM29140,GLM29141,GLM29142,GLM29143,GLM29147,GLM29148,GLM29149,GLM29152,GLM29163,GLM29170,GLM29171,GLM29172,GLM30003,GLM30004,GLM30005,GLM30006,GLM30007,GLM30009,GLM30011,GLM30012,GLM30014,GLM30015,GLM30019,GLM32001,GLM32002,GLM32004,GLM32005,GLM32006,GLM32019,GLM32025,GLM32032,GLM32035,GLM32036,GLM32038,GLM32040,GLM32043,GLM32044,GLM32045,GLM32058,GLM32060,GLM32062,GLM32063,GLM32072,GLM32073,GLM32074,GLM32076,GLM32080,GLM32102,GLM32103,GLM32104,GLM32105,GLM32106,GLM32107,GLM32108,GLM32109,GLM32110,GLM32111,GLM32112,GLM32113,GLM32114,GLM32115,GLM32116,GLM32117,GLM32119,GLM32121,GLM32122,GLM32123,GLM33003,GLM33004,GLM33006,GLM33008,GLM34001,GLM34002,GLM34004,GLM34005,GLM34006,GLM36002,GLM36005,GLM36008,GLM36012,GLM36013,GLM37001,GLM37003,GLM37006,GLM37008,GLM37009,GLM37011,GLM37012,GLM37013,GLM37014,GLM37015,GLM50001,GLM50002,GLM50003,GLM50004,GLM50005,GLM50006,GLM50007,GLM50008,GLM50009,GLM50010,GLM50012,GLM50013,GLM50014,GLM50015,GLM50018,GLM50019,GLM50020,GLM50021,GLM50022,GLM50023,GLM50025,GLM50026,GLM50027,GLM50029,GLM50033,GLM50034,GLM50038,GLM50039,GLM50040,GLM50041,GLM50042,GLM50044,GLM50045,GLM50047,GLM50049,GLM50050,GLM50051,GLM50052,GLM50054,GLM50055,GLM50056,GLM50057,GLM50060,GLM50061,GLM50062,GLM50063,GLM50064,GLM50065,GLM50067,GLM50068,GLM50072,GLM50075,GLM50077,GLM50080,GLM50082,GLM50086,GLM50087,GLM50088,GLM50093,GLM50094,GLM50095,GLM50097,GLM50098,GLM50099,GLM50100,GLM50101,GLM50102,GLM50104,GLM50105,GLM50106,GLM50107,GLM50108,GLM50109,GLM50110,GLM50111,GLM50112,GLM50113,GLM50114,GLM50115,GLM50116,GLM50117,GLM50118,GLM50119,GLM50120,GLM50121,GLM50122,GLM50123,GLM50124,GLM50125,GLM50126,GLM50127,GLM50200,GLM50201,GLM50202,GLM50203,GLM50301,GLM50302,GLM50303,GLM50304,GLM50305,GLM50306,GLM50307,GLM50308,GLM50310,GLM50311,GLM50312,GLM50314,GLM50315,GLM50317,GLM50318,GLM50319,GLM50997,GLM50998,GLM50999,GLM51107,GLM51109,GLM51113,GLM52001,GLM52002,GLM52003,GLM52004,GLM52005,GLM52006,GLM52007,GLM52008,GLM52009,GLM52010,GLM52011,GLM52014,GLM52015,GLM52016,GLM52017,GLM52021,GLM52022,GLM52023,GLM52024,GLM52026,GLM52027,GLM52028,GLM52029,GLM52031,GLM52032,GLM52033,GLM52034,GLM52035,GLM52038,GLM52039,GLM52040,GLM52041,GLM52042,GLM52043,GLM52047,GLM52048,GLM52050,GLM52052,GLM52053,GLM52054,GLM52055,GLM52056,GLM52057,GLM52059,GLM52060,GLM52064,GLM52065,GLM52070,GLM52071,GLM52072,GLM52073,GLM52074,GLM52998,GLM52999,GLM53001,GLM53002,GLM53004,GLM53005,GLM53006,GLM53007,GLM53008,GLM53009,GLM53010,GLM53011,GLM53012,GLM53014,GLM53016,GLM53019,GLM53020,GLM53021,GLM53022,GLM53024,GLM53026,GLM53027,GLM53029,GLM53030,GLM53031,GLM53034,GLM53037,GLM53038,GLM53040,GLM53043,GLM53044,GLM53047,GLM53048,GLM53049,GLM53050,GLM53051,GLM53052,GLM53053,GLM53054,GLM53055,GLM53056,GLM53057,GLM53058,GLM53059,GLM53060,GLM53061,GLM53062,GLM53063,GLM53064,GLM53065,GLM53066,GLM53067,GLM53068,GLM53069,GLM53070,GLM53071,GLM53072,GLM53075,GLM53076,GLM53077,GLM53079,GLM53080,GLM53081,GLM53084,GLM53085,GLM53086,GLM53088,GLM53089,GLM53090,GLM54051,GLM56001,GLM56003,GLM56004,GLM56008,GLM56009,GLM56010,GLM56012,GLM56013,GLM56014,GLM56015,GLM56016,GLM56017,GLM56018,GLM56019,GLM56020,GLM56021,GLM56023,GLM56024,GLM56025,GLM56026,GLM56027,GLM56028,GLM56030,GLM56032,GLM56033,GLM56034,GLM56035,GLM56039,GLM56040,GLM56041,GLM57043,GLM70001,GLM70003,GLM70004,GLM70005,GLM70006,GLM70009,GLM70010,GLM80029,GLM90051,GLM91013,GLM91014,GLM91015,GLM91016,GLM91017,GLM91018,GLM91019,GLM91022,GLM91024,GLM91025,GLM91027,GLM91028,GLM91032";
//		for(String r:rulestr.split(",")){
//			checkRule.add(r);
//		}
		checkRule.add("GLM01004");
		this.setCheckRules(checkRule);
		AbstractJobRequest validation = JobCreateStrategy.createJobRequest("gdbValidation", null);
		validation.setAttrValue("grids", gridIds);
		validation.setAttrValue("rules", JSONArray.fromObject(this.checkRules));
		validation.setAttrValue("targetDbId", targetDbId);
		validation.setAttrValue("timeOut", 300);
		subJobRequests.put("validation", validation);
		//batch
		List<String> batchRule=new ArrayList<String>();
//		batchRule.add("BATCH_TUNNELNAME");
//		batchRule.add("BATCH_SLE");
//		batchRule.add("BATCH_VECTOR_ATTR");
		batchRule.add("BATCH_LANE_PARKING_FLAG");
//		batchRule.add("MILEAGEPILE_NAMECODE");
		
		this.setBatchRules(batchRule);
		AbstractJobRequest batch = JobCreateStrategy.createJobRequest("gdbBatch", null);
		batch.setAttrValue("grids", gridIds);
		batch.setAttrValue("rules", JSONArray.fromObject(this.batchRules));
		batch.setAttrValue("targetDbId", targetDbId);
		subJobRequests.put("batch", batch);
	}

	@Override
	protected int myStepCount() throws JobException {
		return 1;
	}

}
