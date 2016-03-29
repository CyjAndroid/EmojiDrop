package com.example.emojidrop;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private ArrayList<Message> dateList;
	private ListView mListView;
	private RelativeLayout rl;
	private Button mBtnLeft;
	private Button mBtnRight;
	private EditText mEdit;
	private MyListAdapter myListAdapter;
	private EmojiView emojiView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();
		initData();

	}

	private void initData() {
		dateList = new ArrayList<Message>();
		dateList.add(new Message("aaaaaa", 0));//left:0  right:1
		dateList.add(new Message("aaaaaa", 1));
		dateList.add(new Message("aaaaaa", 1));
		dateList.add(new Message("aaaaaa", 0));

		myListAdapter = new MyListAdapter();
		mListView.setAdapter(myListAdapter);
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.list_id);
		rl = (RelativeLayout) findViewById(R.id.rl);
		mBtnLeft = (Button) findViewById(R.id.btn_left);
		mBtnRight = (Button) findViewById(R.id.btn_right);
		mEdit = (EditText) findViewById(R.id.edit);

		mBtnLeft.setOnClickListener(this);
		mBtnRight.setOnClickListener(this);
		
		mListView.setOnTouchListener(new OnTouchListener() {
			float startY = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					float y = event.getY();
					float moveY = y -startY ;
					startY = y;
					//获取手指滑动的偏移量 使动画不会位置错乱
					if(emojiView!=null){
						LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						layoutParams.topMargin = (int) (emojiView.getTop()+moveY);
						emojiView.setLayoutParams(layoutParams);
					}
				
					break;
				case MotionEvent.ACTION_UP:
					
					break;

				}
				return false;
			}
		});
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @return
	 */
	public int getStatusBarHeight() {
		int result = 0;
		int resourceId = getResources().getIdentifier("status_bar_height",
				"dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dateList.size();
		}

		@Override
		public Object getItem(int position) {
			return dateList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder;
			int type = getItemViewType(position);
			if (convertView == null) {
				if (0 == type)
					convertView = View.inflate(MainActivity.this,
							R.layout.item_left, null);
				else {
					convertView = View.inflate(MainActivity.this,
							R.layout.item_right, null);
				}
				mViewHolder = createViewHolder(convertView);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			mViewHolder.mTextView.setText(dateList.get(position).getContent());

			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return dateList.get(position).getLocation();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

	}

	private ViewHolder createViewHolder(View view) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.mTextView = (TextView) view.findViewById(R.id.item_text);
		return viewHolder;
	}

	class ViewHolder {
		private TextView mTextView;
	}

	@Override
	public void onClick(View v) {
		if(!mEdit.getText().toString().isEmpty()){
			switch (v.getId()) {
			case R.id.btn_left:
				dateList.add(new Message(mEdit.getText().toString(), 0));
				break;
			case R.id.btn_right:
				dateList.add(new Message(mEdit.getText().toString(), 1));
				break;

			}
			myListAdapter.notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					//在这过滤是否有关键词，有则开启动画
					if("aa".equals(mEdit.getText().toString())){
						int firstVisiblePosition = mListView.getFirstVisiblePosition();

						int lastVisiblePosition = mListView.getLastVisiblePosition();
						ArrayList<Point> arrayList = new ArrayList<Point>();
						for (int i = 0, currentPos = firstVisiblePosition; i <= lastVisiblePosition-firstVisiblePosition; i++,currentPos++) {
							View v1 = mListView.getChildAt(i);
							if(v1==null){
								return;
							}
							View tx = v1.findViewById(R.id.item_text);
							int[] p = new int[2];
							tx.getLocationInWindow(p);

							Point point = new Point();
							if (0 == dateList.get(currentPos).getLocation()) {
								point.setLocation(dateList.get(currentPos).getLocation());
								point.setXY(p[0] + 100, p[1] - getStatusBarHeight() - i);
							} else {
								point.setLocation(dateList.get(currentPos).getLocation());
								point.setXY(600, p[1] - getStatusBarHeight() - i);
							}
							arrayList.add(point);
						}

						emojiView = new EmojiView(MainActivity.this);
						rl.addView(emojiView,1);
						emojiView.setPoint(arrayList, arrayList.get(0).getLocation());
						emojiView.startPathAnim(1500);
					}
				}
			}, 1000);
		}
	}
}
