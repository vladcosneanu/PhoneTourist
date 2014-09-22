package com.avallon.phonetourist.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.avallon.phonetourist.R;
import com.avallon.phonetourist.items.LandmarkReview;

public class ReviewsListAdapter extends ArrayAdapter<LandmarkReview> {

    private Context context;
    private List<LandmarkReview> reviews;
    private int layoutResource;

    public ReviewsListAdapter(Context context, int resource, List<LandmarkReview> reviews) {
        super(context, resource, reviews);

        this.context = context;
        this.layoutResource = resource;
        this.reviews = reviews;
    }

    public LandmarkReview getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LandmarkReview review = getItem(position);
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(layoutResource, parent, false);
            ViewHolder viewHolder = new ViewHolder();

            viewHolder.personImage = (ImageView) rowView.findViewById(R.id.person_image);
            viewHolder.reviewerName = (TextView) rowView.findViewById(R.id.reviewer_name);
            viewHolder.ratingBar = (RatingBar) rowView.findViewById(R.id.landmark_rating_value);
            viewHolder.reviewDate = (TextView) rowView.findViewById(R.id.review_date);
            viewHolder.reviewText = (TextView) rowView.findViewById(R.id.review_text);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.reviewerName.setText(review.getAuthorName());
        viewHolder.ratingBar.setRating((float) review.getRating());
        viewHolder.reviewDate.setText(review.getTime() + "");
        // viewHolder.reviewText.setText(review.getText());
        viewHolder.reviewText
                .setText("This is a very long review text, posted by a very cool user that likes to type a lot of words. This is a very long review text, posted by a very cool user that likes to type a lot of words. This is a very long review text, posted by a very cool user that likes to type a lot of words. This is a very long review text, posted by a very cool user that likes to type a lot of words.");
        return rowView;
    }

    static class ViewHolder {
        private ImageView personImage;
        private TextView reviewerName;
        private RatingBar ratingBar;;
        private TextView reviewDate;
        private TextView reviewText;
    }
}
