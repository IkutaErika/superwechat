/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import java.util.Hashtable;
import java.util.Map;

import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;
import cn.ucai.superwechat.widget.Constant;
import cn.ucai.superwechat.widget.SuperwechatHelper;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.db.InviteMessgeDao;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.widget.ContactItemView;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * contact list
 * 
 */
public class ContactListFragment extends EaseContactListFragment {
	
    private static final String TAG = ContactListFragment.class.getSimpleName();
    private ContactSyncListener contactSyncListener;
    private ContactInfoSyncListener contactInfoSyncListener;
    private View loadingView;
    private ContactItemView applicationItem;
    private InviteMessgeDao inviteMessgeDao;

    @SuppressLint("InflateParams")
    @Override
    protected void initView() {
        super.initView();
        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.em_contacts_header, null);
        HeaderItemClickListener clickListener = new HeaderItemClickListener();
        applicationItem = (ContactItemView) headerView.findViewById(R.id.application_item);
        applicationItem.setOnClickListener(clickListener);
        headerView.findViewById(R.id.group_item).setOnClickListener(clickListener);
   //     headerView.findViewById(R.id.chat_room_item).setOnClickListener(clickListener);
  //      headerView.findViewById(R.id.robot_item).setOnClickListener(clickListener);
        listView.addHeaderView(headerView);
        //add loading view
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);
        registerForContextMenu(listView);
    }
    
    @Override
    public void refresh() {
    //  Map<String, EaseUser> m = SuperwechatHelper.getInstance().getContactList();
      Map<String, User> m2 = SuperwechatHelper.getInstance().getAppContactList();
    /*   if (m instanceof Hashtable<?, ?>) {
            //noinspection unchecked
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>)m).clone();
        }*/
       if (m2 instanceof Hashtable<?, ?>) {
            //noinspection unchecked
           m2 = (Map<String, User>) ((Hashtable<String, User>)m2).clone();
        }
  //    setContactsMap(m);
      setAppContactsMap(m2);
        super.refresh();
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(getActivity());
        }
        if(inviteMessgeDao.getUnreadMessagesCount() > 0){
            applicationItem.showUnreadMsgView();
        }else{
            applicationItem.hideUnreadMsgView();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    protected void setUpView() {
        titleBar.setRightImageResource(R.drawable.em_add);
        titleBar.setRightLayoutClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), AddContactActivity.class));
                NetUtils.hasDataConnection(getActivity());
            }
        });
        titleBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        hideTitleBar();
        //设置联系人数据
   /*   Map<String, EaseUser> m = SuperwechatHelper.getInstance().getContactList();
      if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>)m).clone();
        }
        setContactsMap(m);
        super.setUpView();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser)listView.getItemAtPosition(position);
                if (user != null) {
                    String username = user.getUsername();
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
                    User user1=SuperwechatHelper.getInstance().getAppContactList().get(username);
                    MFGT.gotoFrientProfile(getActivity(),user1);
                }
            }
        });*/
      Map<String, User> m2 = SuperwechatHelper.getInstance().getAppContactList();
      if (m2 instanceof Hashtable<?, ?>) {
          m2 = (Map<String, User>) ((Hashtable<String, User>)m2).clone();
        }
      //  setContactsMap(m);
        setAppContactsMap(m2);
        super.setUpView();
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User)listView.getItemAtPosition(position);
                if (user != null) {
                    String username = user.getMUserName();
                    // demo中直接进入聊天页面，实际一般是进入用户详情页

                    if (username.equals(EMClient.getInstance().getCurrentUser()))
                    {
                        MFGT.gotoUsersProfile(getActivity(),user.getMUserName());

                    }
                    else {
                        MFGT.gotoFrientProfile(getActivity(),user.getMUserName());
                    }
                }
            }
        });

        
        // 进入添加好友页
        titleBar.getRightLayout().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddContactActivity.class));
            }
        });
        
        
        contactSyncListener = new ContactSyncListener();
        SuperwechatHelper.getInstance().addSyncContactListener(contactSyncListener);
        contactInfoSyncListener = new ContactInfoSyncListener();
        SuperwechatHelper.getInstance().getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);
        if (SuperwechatHelper.getInstance().isContactsSyncedWithServer()) {
            loadingView.setVisibility(View.GONE);
        } else if (SuperwechatHelper.getInstance().isSyncingContactsWithServer()) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contactSyncListener != null) {
            SuperwechatHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }
        if(contactInfoSyncListener != null){
            SuperwechatHelper.getInstance().getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
    }
    
	
	protected class HeaderItemClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.application_item:
                // 进入申请与通知页面
                startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                break;
            case R.id.group_item:
                // 进入群聊列表页面
                startActivity(new Intent(getActivity(), GroupsActivity.class));
                break;
    /*        case R.id.chat_room_item:
                //进入聊天室列表页面
                startActivity(new Intent(getActivity(), PublicChatRoomsActivity.class));
                break;
            case R.id.robot_item:
                //进入Robot列表页面
                startActivity(new Intent(getActivity(), RobotsActivity.class));
                break;
*/
            default:
                break;
            }
        }
	    
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    toBeProcessUser = (User) listView.getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position);
	    toBeProcessUsername = toBeProcessUser.getMUserName();
		getActivity().getMenuInflater().inflate(R.menu.em_context_contact_list, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
            try {
                // delete contact
                deleteContact(toBeProcessUser);
                // remove invitation message
                InviteMessgeDao dao = new InviteMessgeDao(getActivity());
                dao.deleteMessage(toBeProcessUser.getMUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
		return super.onContextItemSelected(item);
	}


	/**
	 * delete contact
	 * 
	 * @param tobeDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser) {
		String st1 = getResources().getString(R.string.deleting);
		final String st2 = getResources().getString(R.string.Delete_failed);
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
        NetDao.deleteContact(getActivity(),SuperwechatHelper.getInstance().getCurrentuser().getMUserName(),tobeDeleteUser.getMUserName(), new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                if (result!=null)
                {
                    Result re= ResultUtils.getResultFromJson(result,User.class);
                    if (re!=null&&re.isRetMsg())
                    {
                        CommonUtils.showShortToast("删除成功！");
                        SuperwechatHelper.getInstance().getAppContactList().remove(tobeDeleteUser.getMUserName());
                        UserDao dao =new UserDao(getActivity());
                        dao.deleteAppContact(tobeDeleteUser.getMUserName());
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });

        new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().contactManager().deleteContact(tobeDeleteUser.getMUserName());
					// remove user from memory and database
					UserDao dao = new UserDao(getActivity());
					dao.deleteContact(tobeDeleteUser.getMUserName());
                    dao.deleteAppContact(tobeDeleteUser.getMUserName());
					SuperwechatHelper.getInstance().getContactList().remove(tobeDeleteUser.getMUserName());
					SuperwechatHelper.getInstance().getAppContactList().remove(tobeDeleteUser.getMUserName());

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							contactList.remove(tobeDeleteUser);
                            AppcontactList.remove(tobeDeleteUser);
							contactListLayout.refresh();

						}
					});
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getActivity(), st2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});

				}

			}
		}).start();

	}
	
	class ContactSyncListener implements SuperwechatHelper.DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            if(success){
                                loadingView.setVisibility(View.GONE);
                                refresh();
                            }else{
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getActivity(), s1, Toast.LENGTH_LONG).show();
                                loadingView.setVisibility(View.GONE);
                            }
                        }
                        
                    });
                }
            });
        }
    }

    class ContactInfoSyncListener implements SuperwechatHelper.DataSyncListener {

        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contactinfo list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    loadingView.setVisibility(View.GONE);
                    if(success){
                        refresh();
                    }
                }
            });
        }
        
    }
	
}
