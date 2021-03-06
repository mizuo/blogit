package controllers;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 制御処理の認証ヘルパー群です。
 * @author mizuo
 */
class ControllerAuthHelpers {

	private ControllerAuthHelpers() {}

	/**
	 * パスワードヘルパーです。
	 * @author mizuo
	 */
	static class PasswordHelper {
		/**
		 * 平文パスワードに salt を加えてハッシュ化します。
		 * 32 文字を超えた平文パスワードの場合は動作保証しません。
		 * @param plain 平文パスワード
		 * @return 平文パスワードに salt を加えてハッシュ化した文字列
		 */
		static String hash(String plain) {
			final String salt = BCrypt.gensalt();
			final String hashed = BCrypt.hashpw(plain, salt);
			return hashed;
		}
		/**
		 * パスワードをハッシュ化した結果が一致するか判定します。
		 * @param plain パスワード
		 * @param hashed パスワードをハッシュ化した文字列
		 * @return 一致する場合 true 
		 */
		static boolean equal(String plain, String hashed) {
			return BCrypt.checkpw(plain, hashed);
		}
	}

	/**
	 * 仮パスワードヘルパーです。
	 * 仮パスワードの生成とハッシュ化処理を支援します。
	 * このクラスで登場する主な用語は次の通りです。
	 * <dl>
	 * <dt>仮登録コード</dt><dd>利用者が任意に設定する 16 字以内の文字列</dd>
	 * <dt>仮パスワード</dt><dd>このクラスが自動生成する 8 文字から 16 文字の長さのランダムな英数字</dd>
	 * <dt>仮登録パスワード</dt><dd>仮登録コードと仮パスワードを結合した文字列</dd>
	 * <dt>パスワード</dt><dd>ログイン処理で使用する文字列</dd>
	 * </dl>
	 * 16 文字を超えた仮登録コードの場合は動作保証しません。
	 * @author mizuo
	 */
	static class TemporaryPasswordHelper {
		/** 仮登録パスワードのフォーマット */
		private static final String INITIALIZE_FORMAT = "%s%s%s";
		/**
		 * 仮登録パスワードを生成します。
		 * @param temporaryCode 仮登録コード
		 * @param plainTemporary 仮パスワード
		 * @return 仮登録パスワード
		 */
		private static String createPlain(String temporaryCode, String plainTemporary) {
			final String plain = String.format(INITIALIZE_FORMAT, temporaryCode, plainTemporary, temporaryCode);
			return plain;
		}
		/** 仮パスワード */
		String plainTemporary;
		/** 仮登録パスワード */
		String plain;
		/**
		 * {@link #plainTemporary 仮パスワード} を再生成します。
		 * @return 仮パスワード
		 */
		@SuppressWarnings("deprecation")
		private String resetPlainTemporary() {
			this.plainTemporary = org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric(8, 16);
			return this.plainTemporary;
		}
		/**
		 * {@link #plain 仮登録パスワード} を再設定します。
		 * @param temporaryCode 仮登録コード
		 * @param plainTemporary 仮パスワード
		 * @return 仮登録パスワード
		 */
		private String resetPlain(String temporaryCode, String plainTemporary) {
			this.plainTemporary = plainTemporary;
			this.plain = createPlain(temporaryCode, plainTemporary);
			return this.plain;
		}
		/**
		 * 仮登録コードと再生成した {@link #plainTemporaryPassword 仮パスワード} を組み合わせた
		 * {@link #plain 仮登録パスワード} を設定し、さらに salt を混ぜてハッシュ化します。
		 * 16 文字を超えた仮登録コードの場合は動作保証しません。
		 * @param temporaryCode 仮登録コード
		 * @return 仮登録パスワードをハッシュ化した文字列
		 */
		String hash(String temporaryCode) {
			final String plainTemporary = resetPlainTemporary();
			final String plain = resetPlain(temporaryCode, plainTemporary);
			final String hashed = PasswordHelper.hash(plain);
			return hashed;
		}
		/**
		 * 仮登録コードと仮パスワードを組み合わせた仮登録パスワードをハッシュ化した結果が一致するか判定します。
		 * @param temporaryCode 仮登録コード
		 * @param plainTemporary 仮パスワード
		 * @param hashed 仮登録パスワードをハッシュ化した文字列
		 * @return 一致する場合 true 
		 */
		static boolean equal(String temporaryCode, String plainTemporary, String hashed) {
			final String plain = createPlain(temporaryCode, plainTemporary);
			return PasswordHelper.equal(plain, hashed);
		}
	}

}
