package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private EditText ean;
    private ImageButton eanSearchButton;
    private String eanString;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";


    private String scannedEan;



    public AddBook(){
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(ean!=null) {
            outState.putString(EAN_CONTENT, ean.getText().toString());
            //this.restartLoader();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ean = (EditText) rootView.findViewById(R.id.ean);
        eanSearchButton = (ImageButton) rootView.findViewById(R.id.search_button_ean);

        eanSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Handling of error case- does the device have an internet connection?
                if (Utility.isNetworkAvailable(getActivity())) {
                    String eanString = ean.getText().toString();


                    //catch isbn10 numbers
                    if (eanString.length() == 10 && !eanString.startsWith("978")) {
                        eanString = "978" + ean;
                    }
                    if (eanString.length() < 13) {
                        clearFields();
                        Toast.makeText(getActivity(), R.string.reenter_ean, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Once we have an ISBN, start a book intent
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.putExtra(BookService.EAN, eanString);
                    bookIntent.setAction(BookService.FETCH_BOOK);
                    getActivity().startService(bookIntent);
                    AddBook.this.restartLoader();

                } else {
                    Context context = getActivity();
                    String text = context.getString(R.string.enable_connectivity);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.


                //Check for internet connectivity before launching barcode scanner
                if(Utility.isNetworkAvailable(getActivity())) {
                    Intent cameraIntent = new Intent(getActivity(), CameraActivity.class);

                    startActivityForResult(cameraIntent, 1);
                } else {
                    Toast.makeText(getActivity(), R.string.enable_connectivity, Toast.LENGTH_SHORT).show();
                }

            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ean.getText().length() == 0) {
                    Toast.makeText(getActivity(), R.string.empty_search_box, Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    ean.setText("");
                    Toast.makeText(getActivity(), R.string.book_added, Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //error case, if the search box is empty
                if(ean.getText().toString().length() == 0) {

                    Toast.makeText(getActivity(), R.string.empty_search_box, Toast.LENGTH_SHORT).show();
                    clearFields();
                    return;
                } else {
                    Intent bookIntent = new Intent(getActivity(), BookService.class);
                    bookIntent.putExtra(BookService.EAN, ean.getText().toString());
                    bookIntent.setAction(BookService.DELETE_BOOK);
                    getActivity().startService(bookIntent);
                    ean.setText("");
                    Toast.makeText(getActivity(), R.string.book_not_added, Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }
        });

        if(savedInstanceState!=null){
            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            ean.setHint("");
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            scannedEan = data.getStringExtra("ean");
            ean.setText(scannedEan);
            this.restartLoader();
        }
    }

    public void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(ean.getText().length()==0){
            return null;
        }
        String eanStr= ean.getText().toString();
        if(eanStr.length()==10 && !eanStr.startsWith("978")){
            eanStr="978"+eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
        ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",","\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);

        //data.close();

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }
}
