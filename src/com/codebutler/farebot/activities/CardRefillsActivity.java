/*
 * CardRefillsActivity.java
 *
 * Copyright (C) 2011 Eric Butler
 *
 * Authors:
 * Eric Butler <eric@codebutler.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.codebutler.farebot.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.codebutler.farebot.R;
import com.codebutler.farebot.mifare.MifareCard;
import com.codebutler.farebot.transit.Refill;
import com.codebutler.farebot.transit.TransitData;

import java.text.DateFormat;
import java.util.Date;

public class CardRefillsActivity extends ListActivity
{
    private MifareCard mCard;

    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_refills);

        mCard = (MifareCard) getIntent().getParcelableExtra(AdvancedCardInfoActivity.EXTRA_CARD);

        TransitData transitData = mCard.parseTransitData();

        if (transitData.getTrips() != null)
            setListAdapter(new RefillsListAdapter(this, transitData.getRefills()));
        else {
            findViewById(android.R.id.list).setVisibility(View.GONE);
            findViewById(R.id.error_text).setVisibility(View.VISIBLE);
        }
    }

    private static class RefillsListAdapter extends ArrayAdapter<Refill>
    {
        public RefillsListAdapter (Context context, Refill[] refills)
        {
            super(context, 0, refills);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent)
        {
            Activity activity = (Activity) getContext();
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.refill_item, null);
            }

            Refill refill = (Refill) getItem(position);
            Date date = new Date(refill.getTimestamp() * 1000);

            TextView dateTextView    = (TextView) convertView.findViewById(R.id.date_text_view);
            TextView timeTextView    = (TextView) convertView.findViewById(R.id.time_text_view);
            TextView agencyTextView   = (TextView) convertView.findViewById(R.id.agency_text_view);
            TextView amountTextView    = (TextView) convertView.findViewById(R.id.amount_text_view);

            dateTextView.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(date));
            timeTextView.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
            agencyTextView.setText(refill.getShortAgencyName());
            amountTextView.setText(refill.getAmountString());

            return convertView;
        }
    }
}