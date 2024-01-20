package com.example.distributedapplicationsproject.android.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.distributedapplicationsproject.R;
import org.jetbrains.annotations.NotNull;

public class CustomProgressDialog extends DialogFragment {

    ViewGroup contentView;
    String title;
    ProgressBar progressBar;
    TextView textView;
    View blockingOverlay;

    public CustomProgressDialog(ViewGroup contentView, String title) {
        super();
        this.contentView = contentView;
        this.title = title;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = initDialogView();

        builder.setView(view);

        // Create the AlertDialog object and return it.
        AlertDialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        blockingOverlay = initBlockingOverlay();
        contentView.addView(blockingOverlay);

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        contentView.removeView(blockingOverlay);
        this.getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private View initDialogView() {
        // Get the layout inflater.
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_progress, null);

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setProgress(0);

        textView = view.findViewById(R.id.text_title);
        textView.setText(title);
        
        return view;
    }

    private View initBlockingOverlay() {
        // Create a transparent overlay
        View overlay = new View(contentView.getContext());
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#88000000")); // Semi-transparent black
        overlay.setClickable(true);
        return overlay;
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress, true);
    }

}
