package hoeflechner.moser.stix.voglbauer.schiebepuzzle;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<Button> myButtons;
    private int myColumnWidth, myColumnHeight;

    public CustomAdapter(ArrayList<Button> myButtons, int columnWidth, int columnHeight) {
        this.myButtons = myButtons;
        myColumnWidth = columnWidth;
        myColumnHeight = columnHeight;

    }

    @Override
    public int getCount() {
        return myButtons.size();
    }

    @Override
    public Object getItem(int position) {
        return(Object) myButtons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;

        if(convertView == null){
            button=myButtons.get(position);
        } else{
            button=(Button) convertView;
        }
        AbsListView.LayoutParams params=new AbsListView.LayoutParams(myColumnWidth, myColumnHeight);
        button.setLayoutParams(params);
        return button;
    }
}
