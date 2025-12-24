package jp.co.sss.crud.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jp.co.sss.crud.entity.Employee;

/**
 * 権限認証用フィルタ
 * 
 * @author System Shared
 */
public class AdminAccountCheckFilter extends HttpFilter {

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// URIと送信方式を取得する
		String requestURI = request.getRequestURI();
		String requestMethod = request.getMethod();

		// 完了画面はフィルターを通過させる
		if (requestURI.contains("/complete") && requestMethod.equals("GET")) {
			chain.doFilter(request, response);
			return;
		}

		//TODO セッションからユーザー情報を取得
		HttpSession session = request.getSession();
		Employee emp = (Employee) session.getAttribute("user");
		//TODO セッションユーザーのIDと権限の変数をそれぞれ初期化
		int empId = -1;
		int authority = -1;
		//TODO セッションユーザーがNULLでない場合
		if (emp != null) {
			//TODO セッションユーザーからID、権限を取得して変数に代入
			empId = emp.getEmpId();
			authority = emp.getAuthority();
		}

		//TODO  更新対象の社員IDをリクエストから取得
		String updateEmpIdStr = request.getParameter("empId");
		int updateEmpId = 0;
		//TODO  社員IDがNULLでない場合
		if (updateEmpIdStr != null) {
			//TODO 社員IDを整数型に変換
			updateEmpId = Integer.parseInt(updateEmpIdStr);
		}

		//フィルター通過のフラグを初期化 true:フィルター通過 false:ログイン画面へ戻す
		boolean accessFlg = false;

		//TODO  削除（削除確認/削除実行）は管理者のみ許可
		if (authority == 2) {
			accessFlg = true;
			//TODO  更新は管理者または本人のみ許可
		} else if (empId == updateEmpId) {
			if (!requestURI.contains("/delete")) {
				accessFlg = true;
			}
		}

		//TODO  accessFlgが立っていない場合はログイン画面へリダイレクトし、処理を終了する
		if (accessFlg == false) {
			//TODO  レスポンス情報を取得
			String URI = "/spring_crud/";
			//TODO  ログイン画面へリダイレクト
			response.sendRedirect(URI);
			//処理を終了
			return;
		}

		chain.doFilter(request, response);
		return;

	}

}
