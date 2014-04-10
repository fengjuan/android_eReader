package com.example.minireader.file;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.minireader.R;
import com.example.minireader.entity.BookInfo;
import com.example.minireader.sqlite.DbHelper;

public class ChooseFileActivity extends ListActivity {
	private static final String TAG = "ChooseFileActivity";
	static private File mDirectory;// 当前的文件夹
	static private Map<String, Integer> mPositions = new HashMap<String, Integer>();
	private File mParent;// 父文件夹
	private File[] mDirs;// 当前目录中的文件夹
	private File[] mFiles;// 当前目录中的文件
	private Handler mHandler;// 执行runnable的handler
	private Runnable mUpdateFiles;// 更新当前目录的runnable
	private ChooseFileAdapter mAdapter;
	private List<File> mSelectFiles; //选中的文件
	
	private DbHelper mDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDb = new DbHelper(this);
		mSelectFiles = new ArrayList<File>();

		if (mDirectory == null)		//若目录为空 默认为sdcard
			mDirectory = Environment.getExternalStorageDirectory().getAbsoluteFile();

		// 加载文件
		mHandler = new Handler();
		mUpdateFiles = new Runnable() {
			public void run() {
				Resources res = getResources();
				String appName = res.getString(R.string.app_name);
				String version = res.getString(R.string.version);
				String title = res.getString(R.string.picker_title_App_Ver_Dir);
				setTitle(String.format(title, appName, version, mDirectory));

				mParent = mDirectory.getParentFile();

				mDirs = mDirectory.listFiles(new FileFilter() {

					public boolean accept(File file) {
						return file.isDirectory();
					}
				});
				if (mDirs == null)
					mDirs = new File[0];

				mFiles = mDirectory.listFiles(new FileFilter() {

					public boolean accept(File file) {
						if (file.isDirectory())
							return false;
						String fname = file.getName().toLowerCase();
						if (fname.endsWith(".txt"))
							return true;
						if (fname.endsWith(".doc"))
							return true;
						if (fname.endsWith(".docx"))
							return true;
						if (fname.endsWith(".pdf"))
							return true;
						return false;
					}
				});
				
				// 创建ListView的mAdapter
				if (mFiles == null)
					mFiles = new File[0];

				//按文件名排序
				Arrays.sort(mFiles, new Comparator<File>() {
					public int compare(File arg0, File arg1) {
						return arg0.getName().compareToIgnoreCase(arg1.getName());
					}
				});

				Arrays.sort(mDirs, new Comparator<File>() {
					public int compare(File arg0, File arg1) {
						return arg0.getName().compareToIgnoreCase(arg1.getName());
					}
				});

				mAdapter.clear();
				//将目录和文件都加到adapter中
				if (mParent != null)
					mAdapter.add(new ChooseFileItem(ChooseFileItem.Type.PARENT, getString(R.string.parent_directory)));
				for (File f : mDirs)
					mAdapter.add(new ChooseFileItem(ChooseFileItem.Type.DIR, f.getAbsolutePath()));
				for (File f : mFiles)
					mAdapter.add(new ChooseFileItem(ChooseFileItem.Type.DOC, f.getAbsolutePath()));
				lastPosition();
			}
		};

		// 开启线程载入文件列表
		mHandler.post(mUpdateFiles);
		mAdapter = new ChooseFileAdapter(this);
		setListAdapter(mAdapter);

		// 监听文件的变化！这个目录下的文件被删除了或者有了新的文件都要更新一下文件列表
		FileObserver observer = new FileObserver(mDirectory.getPath(), FileObserver.CREATE | FileObserver.DELETE) {
			public void onEvent(int event, String path) {
				mHandler.post(mUpdateFiles);
			}
		};
		observer.startWatching();
	}
	
	

	/**
	 * 菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.import_book_popup, menu);
		return super.onCreateOptionsMenu(menu);
	}



	private void lastPosition() {
		String p = mDirectory.getAbsolutePath();
		if (mPositions.containsKey(p))
			getListView().setSelection(mPositions.get(p));
	}
	

	int checkNum;	//选中的个数
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		//取出ViewHolder对象
		ViewHolder holder = (ViewHolder) v.getTag();
		//改变CheckBox的状态
		holder.isSelected.toggle();
		//将选中状况记录下来
		ChooseFileAdapter.getmIsSelected().put(position, holder.isSelected.isChecked());
		
		if(holder.isSelected.isChecked() == true) {
			String fileName = ((ChooseFileItem)mAdapter.getItem(position)).name;
			File file = new File(fileName);
			if(file.isFile()) {
				mSelectFiles.add(file);
				checkNum ++;
			}
		}
		mPositions.put(mDirectory.getAbsolutePath(), getListView().getFirstVisiblePosition());

		if (position < (mParent == null ? 0 : 1)) {
			mDirectory = mParent;
			mHandler.post(mUpdateFiles);
			return;
		}

		position -= (mParent == null ? 0 : 1);

		if (position < mDirs.length) {
			mDirectory = mDirs[position];
			mHandler.post(mUpdateFiles);
			return;
		}

		position -= mDirs.length;

	}
	
	public void changeCheckAll() {
		//通知listView刷新
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.select_all:
			for (int i = 0; i < mAdapter.getCount(); i++) {
				ChooseFileAdapter.getmIsSelected().put(i, true);
				checkNum++;
			}
			mSelectFiles = Arrays.asList(mFiles);
			changeCheckAll();
			break;
		case R.id.select_cancel:
			for (int i = 0; i < mFiles.length; i++) {
				ChooseFileAdapter.getmIsSelected().put(i, false);
				checkNum = 0;
			}
			mSelectFiles.clear();
			changeCheckAll();
			break;
		case R.id.import_check:
			for (int i = 0; i < mSelectFiles.size(); i++) {
				if(checkNum == 0) {
					Toast.makeText(this, "请选择要导入的文件", Toast.LENGTH_LONG).show(); break;
				} else {
					//数据库操作
					System.out.println(mSelectFiles.get(i).getName());
					BookInfo bookInfo = new BookInfo();
					bookInfo.bookname = mSelectFiles.get(i).getName();
					bookInfo.url = mSelectFiles.get(i).getAbsolutePath();
					bookInfo.bookmark = 0;
					mDb.insert(bookInfo);
					
				}
				ChooseFileAdapter.getmIsSelected().put(i, false);
			}
			changeCheckAll();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}



	@Override
	protected void onPause() {
		super.onPause();
		mPositions.put(mDirectory.getAbsolutePath(), getListView().getFirstVisiblePosition());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Toast.makeText(this, "返回键", 0).show();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
