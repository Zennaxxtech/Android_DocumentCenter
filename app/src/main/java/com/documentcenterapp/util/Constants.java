package com.documentcenterapp.util;


import java.util.HashMap;

public class Constants {

	public static final int CHOOSE_PHOTO = 101;
	public static final int CHOOSE_CAMERA = 102;
	public static final int CHOOSE_DOCUMENT = 103;

	public static final String PRODUCT_IMAGE_NAME = "product_image";

	public class RequestCode {
		public static final int REQUEST_CODE_SELECT_IMAGE = 1;
	}

	// Web services response Code
	public class ServiceCode {
		public static final int SERVICE_DO_NOTHING = 0;
		public static final int SERVICE_LOGIN = 1;
		public static final int SERVICE_EMPTY_LOGIN = 2;
		public static final int SERVICE_PARSE_LIST = 3;
		public static final int SERVICE_DELETE = 4;
		public static final int SERVICE_DELETE_COMPLETED = 5;

		// Used to communicate state changes in the DownloaderThread
		public static final int MESSAGE_DOWNLOAD_STARTED = 21;
		public static final int MESSAGE_DOWNLOAD_COMPLETE = 22;
		public static final int MESSAGE_UPDATE_PROGRESS_BAR = 23;
		public static final int MESSAGE_DOWNLOAD_CANCELED = 24;
		public static final int MESSAGE_CONNECTING_STARTED = 25;
		public static final int MESSAGE_ENCOUNTERED_ERROR = 26;

	}

	public class Intent {
		public static final String INTENT_MENU_INDEX = "menu_index";
	}

	public static class Profile {
		public static boolean refreshProfile = false;
		public static HashMap<String, Object> profileHashMap = new HashMap<String, Object>();
	}

	public class Preferences {
		public static final String PREF_LOGIN_DONE = "login_done";
		public static final String PREF_API_INIT_DONE = "api_init_done";
		public static final String PREF_SECRET_KEY = "secretkey";
		public static final String PREF_APP_NAME = "app_name";
		public static final String PREF_TOKEN = "token";
		public static final String PREF_USER_NAME = "username";
		public static final String PREF_DECREPTED_USER_NAME = "decrepted_username";
		public static final String PREF_USER_PASSWORD = "password";
		public static final String PREF_LOGIN_TYPE = "login_type";
		public static final String PREF_LANGUAGE_SELECT = "selected_language";
		public static final String PREF_CHECK_UNCHECK = "check_uncheck";
		public static final String PREF_STATUS_COPY_MOVE = "copy_move";
		public static final String PREF_FILE_OR_FOLDER_ID = "file_folder_id";
		public static final String PREF_FILE_OR_FOLDER = "file_folder";
		public static final String PREF_FILE_NAME = "file_name";
		public static final String PREF_FILE_DOWNLOAD_LINK = "file_download_link";
		public static final String PREF_FILE_SIZE = "file_size";
		public static final String PREF_FILE_DATE = "file_date";
		public static final String PREF_FILE_ICON = "file_icon";
		public static final String PREF_TOGGLE = "togle_on_off";
	}

	public class ResponseStatusCodeString{
		public final static String SUCCESS = "success";
		public final static String ERROR = "error" ;
		public final static String UNAUTHORIZED = "unauthorized" ;
	}

	public class IntentCode {
		public static final String INTENT_MENU_INDEX = "menu_index";
	}

	public class LoginType {
		public static final String EMAIL = "email";
		public static final String MOBILE = "mobile";
	}

	public class PasswordType {
		public static final String PASSWORD = "password";
		public static final String OTP = "otp";
	}

	public static class Region {
		public static boolean refreshRegionList = false;
		public static boolean refreshStateList = false;
	}

	public static class Category {
		public static boolean refreshCategoryList = false;
	}

	public static class SubCategory {
		public static boolean refreshSubCategoryList = false;
	}

	public static class Product {
		public static boolean refreshProductList = false;
	}

	// for sending email
	public class EMAIL{
		public static final String MAIL_ID = "monparanirav@gmail.com";
		public static final String CHOOSE_EMAIL_CLIENT_TITLE = "Choose an Email client:";
	}

	public final static String ALERT_TITLE = "DOCUMENTCENTERAPP";

	public static final String PRODUCT_IMAGE_PATH = "/DocumentCenterApp/product_images_IntraApp";

	public static final String FILES_UPLOAD_PATH = "/DocumentCenterApp/files_DocumentCenter";

	public static final String FILES_DOWNLOAD_PATH = "/DocumentCenterApp/files_IntraApp";

	public static final String IMAGE_FILE_PATH = "/DocumentCenterApp/images";

	public static final String PROFILE_PIC_DUMMY = "dummy-photo.jpg";

	public static final int VALUE_NOT_PROVIDED = -1;

	public static final char[] HEX_CHARS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

	public static final String VALUE_NOT_PROVIDED_STRING = null;

	public static int CONNECTION_TIMEOUT_IN_SECONDS = 12;

	public static final int DOWNLOAD_BUFFER_SIZE = 4096;

	public class ACRA {
		public static final String CRASH_REPORT_EMAIL_ID = EMAIL.MAIL_ID;
	}

}
