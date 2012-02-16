package net.hubalek.android.gaugebattwidget.themes.ics;

import net.hubalek.android.gaugebattwidget.themes.library.AbstractContentProvider;

/**
 * Class providing info about theme.
 */
public class ContentProvider extends AbstractContentProvider {
    @Override
    protected int getAuthorEmailResourceId() {
        return R.string.email;
    }

    @Override
    protected int getDonationEmailResourceId() {
        return R.string.donation_email;
    }

    @Override
    protected int getDonationAmountResourceId() {
        return R.string.donation_amount;
    }

    @Override
    protected int getDonationCurrencyResourceId() {
        return R.string.donation_currency;
    }

    @Override
    protected int getDescriptionResourceId() {
        return R.string.description;
    }

    @Override
    protected int getAuthorUrlResourceId() {
        return R.string.url;
    }

    @Override
    protected int getAuthorResourceId() {
        return R.string.author;
    }
}
