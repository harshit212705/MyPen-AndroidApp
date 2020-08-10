package application.example.mypen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapterNewFont extends BaseAdapter {

    private Context context;

    private int icons[];
    private String options[];
    private String optionsDescription[];

    LayoutInflater inflter;

    public CustomAdapterNewFont(Context applicationContext, int[] icons, String[] options, String[] optionsDescription) {
        this.context = applicationContext;
        this.icons = icons;
        this.options = options;
        this.optionsDescription = optionsDescription;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.card_layout_new_font, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView option = (TextView) view.findViewById(R.id.option);
        TextView optionDescription = (TextView) view.findViewById(R.id.optionDescription);

        icon.setImageResource(icons[i]);
        option.setText(options[i]);
        optionDescription.setText(optionsDescription[i]);

        return view;
    }

}