package com.navinfo.dataservice.scripts;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.util.Assert;

import com.navinfo.dataservice.commons.config.SystemConfig;
import com.navinfo.dataservice.commons.database.MultiDataSourceFactory;
import com.navinfo.dataservice.man.project.ProjectSelector;
import com.navinfo.dataservice.versionman.lock.FmMesh4Lock;
import com.navinfo.dataservice.versionman.lock.MeshLockManager;

public class BorrowMeshScriptsInterface {

	public static JSONObject initProject(JSONObject request) {
		JSONObject response = new JSONObject();
		try {
			String sourceProjectIdStr = (String) request.get("sourceProjectId");
			Assert.notNull(sourceProjectIdStr, "sourceProjectId不能为空");
			String targetProjectIdStr = (String) request.get("targetProjectId");
			Assert.notNull(targetProjectIdStr, "targetProjectId不能为空");

			int targetProjectId = Integer.valueOf(targetProjectIdStr);
			int sourceProjectId = Integer.valueOf(sourceProjectIdStr);

			String gdbVersion = "240+";
			String meshStr = (String) request.get("meshes");
			meshStr = com.navinfo.navicommons.utils.StringUtils
					.removeBlankChar(meshStr);
			Assert.notNull(meshStr, "meshes不能为空");

			Set<Integer> meshSet = new HashSet<Integer>();

			String[] splits = meshStr.split(",");

			for (String s : splits) {
				meshSet.add(Integer.valueOf(s));
			}

			System.out.println("get dbId");
			// 获取dbId
			ProjectSelector prjselector = new ProjectSelector(
					MultiDataSourceFactory.getInstance().getManDataSource()
							.getConnection());
			int sourceDbId = prjselector.getDbId(sourceProjectId);

			prjselector = new ProjectSelector(MultiDataSourceFactory
					.getInstance().getManDataSource().getConnection());

			int targetDbId = prjselector.getDbId(targetProjectId);

			// 加锁
			System.out.println("locking");
			MeshLockManager man = new MeshLockManager(MultiDataSourceFactory
					.getInstance().getManDataSource());
			man.lock(targetProjectId, meshSet, FmMesh4Lock.TYPE_BORROW);
			response.put("lock_mesh", "success");

			System.out.println("locked");
			// delete data
			JSONObject delRequest = new JSONObject();
			delRequest.put("exportMode", "delete");
			delRequest.put("feature", "gdb");
			delRequest.put("condition", "mesh");
			delRequest.put("conditionParams", meshStr);
			delRequest.put("dataIntegrity", "true");
			delRequest.put("sourceDbId", String.valueOf(targetDbId));
			delRequest.put("gdbVersion", gdbVersion);
			JSONObject delResponse = ToolScriptsInterface
					.exportData(delRequest);
			response.put("delete_data", delResponse);

			System.out.println("deleted");
			// export data
			JSONObject expRequest = new JSONObject();
			expRequest.put("exportMode", "copy");
			expRequest.put("feature", "gdb");
			expRequest.put("condition", "mesh");
			expRequest.put("conditionParams", meshStr);
			expRequest.put("dataIntegrity", "false");
			expRequest.put("sourceDbId", String.valueOf(sourceDbId));
			expRequest.put("targetDbId", String.valueOf(targetDbId));
			expRequest.put("gdbVersion", gdbVersion);
			JSONObject expResponse = ToolScriptsInterface
					.exportData(expRequest);
			response.put("export_data", expResponse);

			System.out.println("exported");
			// 解锁
			man.unlock(targetProjectId, meshSet, FmMesh4Lock.TYPE_BORROW);
			response.put("unlock_mesh", "success");

			System.out.println("unlocked");
			response.put("msg", "执行成功");
		} catch (Exception e) {
			response.put("msg", "ERROR:" + e.getMessage());
		} finally {
		}
		return response;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JSONObject request = null;
			JSONObject response = null;
			String dir = SystemConfig.getSystemConfig().getValue("scripts.dir");
			request = ToolScriptsInterface.readJson(dir + "request"
					+ File.separator + "borrow_mesh.json");
			response = initProject(request);
			ToolScriptsInterface.writeJson(response, dir + "response"
					+ File.separator + "borrow_mesh.json");

			System.out.println(response);
			System.out.println("Over.");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Oops, something wrong...");
			e.printStackTrace();
		}

	}

}
