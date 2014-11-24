package ch.sebastianhaeni.remotecontrol;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

public class MyEditText extends EditText {

	private Random r = new Random();
	private String _ipAddress;
	private Context _context;

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_context = context;
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
	}

	public MyEditText(Context context) {
		super(context);
		_context = context;
	}

	public void setIpAddress(String ipAddress) {
		_ipAddress = ipAddress;
	}

	public void setRandomBackgroundColor() {
		setBackgroundColor(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new MyInputConnection(super.onCreateInputConnection(outAttrs), true);
	}

	private class MyInputConnection extends InputConnectionWrapper {

		public MyInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			(new NetworkThread(_context, _ipAddress, "keyboard_backspace:" + beforeLength, "KEYBOARD_REMOVED")).start();
			return super.deleteSurroundingText(beforeLength, afterLength);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
				MyEditText.this.setRandomBackgroundColor();
			}
			return super.sendKeyEvent(event);
		}

	}

}