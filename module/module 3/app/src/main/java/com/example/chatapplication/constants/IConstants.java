package com.example.chatapplication.constants;

import java.io.File;

public interface IConstants {

    String SDPATH = "/storage/emulated/1/";
    String IMG_FOLDER = "MyImages_v2";
    String SENT_FILE = "/SentFiles_v2/";

    String REF_USERS = "Users_v2";
    String REF_CHATS = "Chats_v3";
    String REF_UPLOAD = "Uploads_v2";

    String REF_GROUP_UPLOAD = "GroupUploads_v2";
    String REF_GROUP_PHOTO_UPLOAD = "GroupPhotos_v2";
    String REF_GROUP_ATTACHMENT = "GroupAttachment_v2";
    String REF_CHAT_PHOTO_UPLOAD = "ChatPhotos_v2";
    String REF_CHAT_ATTACHMENT = "ChatAttachment_v2";
    String REF_VIDEO_THUMBS = "Thumbnails_v2";

    String REF_TOKENS = "Tokens_v2";
    String REF_OTHERS = "Others_v2";

    String REF_GROUPS = "Groups_v2";
    String REF_GROUPS_S = REF_GROUPS + SLASH;
    String REF_GROUP_MEMBERS = "MembersGroups_v2";
    String REF_GROUP_MEMBERS_S = REF_GROUP_MEMBERS + SLASH;
    String REF_GROUPS_MESSAGES = "GroupsMessages_v2";

    String IMG_DEFAULTS = "defaults_v2";

    String EXTRA_USER_ID = "userId_v2";

    String EXTRA_SENDER = "sender_v2";
    String EXTRA_RECEIVER = "receiver_v2";
    String EXTRA_MESSAGE = "message_v2";
    String EXTRA_TYPE = "type_v2";
    String EXTRA_IMGPATH = "imgPath_v2";
    String EXTRA_ATTACH_TYPE = "attachmentType_v2";
    String EXTRA_ATTACH_PATH = "attachmentPath_v2";
    String EXTRA_ATTACH_NAME = "attachmentName_v2";
    String EXTRA_ATTACH_FILE = "attachmentFileName_v2";
    String EXTRA_ATTACH_SIZE = "attachmentSize_v2";
    String EXTRA_ATTACH_DATA = "attachmentData_v2";
    String EXTRA_ATTACH_DURATION = "attachmentDuration_v2";
    String EXTRA_DATETIME = "datetime_v2";

    String EXTRA_ID = "id_v2";
    String EXTRA_EMAIL = "email_v2";
    String EXTRA_USERNAME = "username_v2";
    String EXTRA_PASSWORD = "password_v2";
    String EXTRA_IMAGEURL = "imageURL_v2";
    String EXTRA_ACTIVE = "active_v2";
    String EXTRA_TYPING = "typing_v2";
    String EXTRA_TYPINGWITH = "typingwith_v2";
    String EXTRA_LINK = "linkPath_v2";
    String EXTRA_ABOUT = "about_v2";
    String EXTRA_GENDER = "genders_v2";
    String EXTRA_LASTSEEN = "lastSeen_v2";
    String EXTRA_GROUPS_IN = "groupsIn_v2";
    String EXTRA_GROUPS_IN_BOTH = SLASH + EXTRA_GROUPS_IN + SLASH;

    String EXTRA_GROUP_ID = "groupId_v2";
    String EXTRA_GROUP_NAME = "name_v2";
    String EXTRA_ADMIN = "admin_v2";
    String EXTRA_GROUP_MEMBERS = "members_v2";
    String EXTRA_GROUP_IMG = "groupImg_v2";
    String EXTRA_LAST_MSG = "lastMsg_v2";
    String EXTRA_LAST_TIME = "lastMsgTime_v2";
    String EXTRA_CREATED_AT = "createdAt_v2";
    String EXTRA_SEND_MESSAGES = "sendMessageSetting_v2";

    String EXTRA_OBJ_GROUP = "groupObject_v2";

    boolean TRUE = true;
    boolean FALSE = false;

    String FCM_ICON = "icon_v2";
    String FCM_USER = "user_v2";
    String FCM_SENT = "sent_v2";
    String FCM_TITLE = "title_v2";
    String FCM_BODY = "body_v2";
    String FCM_GROUPS = "groups_v2";
    String FCM_USERNAME = "username_v2";
    String FCM_TYPE = "type_v2";

    long CLICK_DELAY_TIME = 300;
    int EXTRA_TYPING_DELAY = 1000;
    int EXTRA_DELAY = 1500;
    int SPLASH_DELAY = 5000;
    int ZERO = 0; 
    int ONE = 1; 
    int TWO = 2; 
    int THREE = 3;

    int REQUEST_PARTICIPATE = 1487;

    int DELAY_ONE_SEC = 1000;
    int VIBRATE_HUNDRED = 100;
    int REQUEST_CODE_CONTACT = 2002;
    int REQUEST_CODE_PLAY_SERVICES = 2003;
    int PERMISSION_CONTACT = 2014;
    int PERMISSION_AUDIO = 2025;
    int PERMISSION_DOCUMENT = 2058;
    int PERMISSION_VIDEO = 2041;
    int REQUEST_PERMISSION_RECORD = 2059;

    String TYPE_TEXT = "TEXT";
    String TYPE_IMAGE = "IMAGE";
    String TYPE_VIDEO = "VIDEO";

    String DOWNLOAD_DATA = "download_data";
    String BROADCAST_DOWNLOAD_EVENT = "com.bytesbee.firebase.chat.activities.DOWNLOAD_EVENT";

    String PLAYING_DATA = "playing_data";
    String BROADCAST_PLAY_RECORDING_EVENT = "com.bytesbee.firebase.chat.activities.PLAY_RECORDING_EVENT";

    int STARTED = 0;
    int COMPLETED = 1;

    String PATH_ABOUT_US = "about_us.html";
    String PATH_PRIVACY_POLICY = "privacy_policy.html";
    String DEFAULT_UPDATE_URL = "https://play.google.com/store/apps/details?id=";
    String DEFAULT_UPDATE_URL_2 = "market://details?id=";

    // 0 = All Participants, 1 = Only Admin
    int SETTING_ALL_PARTICIPANTS = 0;
    int SETTING_ONLY_ADMIN = 1;

    int GEN_UNSPECIFIED = -1;
    int GEN_MALE = 1;
    int GEN_FEMALE = 2;

    int STATUS_ONLINE = 1;
    int STATUS_OFFLINE = 2;

    //From here, you may configure Private Image Preview. You are free to modify as necessary.
    String IMG_PREVIEW = "https://i.ibb.co/mTX9q3c/img.png"; //"https://i.ibb.co/J3vHTrt/img.png";

    String TYPE_EMAIL = "Email";
    String TYPE_GOOGLE = "Google";
    String EMPTY = "";
}