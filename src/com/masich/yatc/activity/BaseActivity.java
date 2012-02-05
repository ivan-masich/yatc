package com.masich.yatc.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import com.masich.yatc.R;
import com.masich.yatc.UpdaterService;

/**
 * Base class for all activities.
 *
 * @author Masich Ivan <john@masich.com>
 */
public abstract class BaseActivity extends Activity {
    private final int DIALOG_EXIT = 2;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menuExit:
                showDialog(DIALOG_EXIT);

                break;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = super.onCreateDialog(id);
        if (dialog != null) {
            return dialog;
        }

        final Activity activity = this;

        switch (id) {
            case DIALOG_EXIT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(R.string.alertExit);

                builder.setPositiveButton(
                    R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            activity.stopService(new Intent(activity, UpdaterService.class));

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);

                            activity.finish();
                        }
                    }
                );

                builder.setNegativeButton(
                    R.string.no,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    }
                );

                return builder.create();
            default:
                return null;
        }
    }
}
