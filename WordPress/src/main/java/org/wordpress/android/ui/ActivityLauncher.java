package org.wordpress.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import org.wordpress.android.R;

import org.wordpress.android.WordPress;
import org.wordpress.android.analytics.AnalyticsTracker;
import org.wordpress.android.models.Blog;
import org.wordpress.android.models.Post;
import org.wordpress.android.networking.SSLCertsViewActivity;
import org.wordpress.android.networking.SelfSignedSSLCertsManager;
import org.wordpress.android.ui.accounts.NewAccountActivity;
import org.wordpress.android.ui.accounts.SignInActivity;
import org.wordpress.android.ui.comments.CommentsActivity;
import org.wordpress.android.ui.media.MediaBrowserActivity;
import org.wordpress.android.ui.posts.EditPostActivity;
import org.wordpress.android.ui.posts.PagesActivity;
import org.wordpress.android.ui.posts.PostsActivity;
import org.wordpress.android.ui.prefs.BlogPreferencesActivity;
import org.wordpress.android.ui.prefs.SettingsActivity;
import org.wordpress.android.ui.stats.StatsActivity;
import org.wordpress.android.ui.stats.StatsSinglePostDetailsActivity;
import org.wordpress.android.ui.stats.models.PostModel;
import org.wordpress.android.ui.themes.ThemeBrowserActivity;
import org.wordpress.android.util.AppLog;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ActivityLauncher {

    public static void showSitePickerForResult(Activity activity, boolean visibleAccountsOnly) {
        Intent intent = new Intent(activity, SitePickerActivity.class);
        intent.putExtra(SitePickerActivity.ARG_VISIBLE_ONLY, visibleAccountsOnly);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                activity,
                R.anim.activity_slide_in_from_left,
                R.anim.do_nothing);
        ActivityCompat.startActivityForResult(activity, intent, RequestCodes.SITE_PICKER, options.toBundle());
    }

    public static void viewCurrentSite(Context context) {
        Intent intent = new Intent(context, ViewSiteActivity.class);
        context.startActivity(intent);
    }

    public static void viewBlogStats(Context context, int blogLocalTableId) {
        Intent intent = new Intent(context, StatsActivity.class);
        intent.putExtra(StatsActivity.ARG_LOCAL_TABLE_BLOG_ID, blogLocalTableId);
        context.startActivity(intent);
    }

    public static void viewCurrentBlogPosts(Context context) {
        ActivityLauncher.viewCurrentBlogPosts(context, true);
    }

    public static void viewCurrentBlogPosts(Context context, boolean animated) {
        Intent intent = new Intent(context, PostsActivity.class);
        if (!animated) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        context.startActivity(intent);
    }

    public static void viewCurrentBlogMedia(Context context) {
        Intent intent = new Intent(context, MediaBrowserActivity.class);
        context.startActivity(intent);
    }

    public static void viewCurrentBlogPages(Context context) {
        Intent intent = new Intent(context, PagesActivity.class);
        intent.putExtra(PostsActivity.EXTRA_VIEW_PAGES, true);
        context.startActivity(intent);
    }

    public static void viewBlogComments(Context context, Blog blog) {
        Intent intent = new Intent(context, CommentsActivity.class);
        context.startActivity(intent);
    }

    public static void viewCurrentBlogThemes(Context context) {
        Intent intent = new Intent(context, ThemeBrowserActivity.class);
        context.startActivity(intent);
    }

    public static void viewBlogSettings(Context context, Blog blog) {
        Intent intent = new Intent(context, BlogPreferencesActivity.class);
        intent.putExtra("id", blog.getLocalTableBlogId());
        context.startActivity(intent);
    }

    public static void viewBlogAdmin(Context context, Blog blog) {
        AnalyticsTracker.track(AnalyticsTracker.Stat.OPENED_VIEW_ADMIN);

        Intent intent = new Intent(context, WPWebViewActivity.class);
        intent.putExtra(WPWebViewActivity.AUTHENTICATION_USER, blog.getUsername());
        intent.putExtra(WPWebViewActivity.AUTHENTICATION_PASSWD, blog.getPassword());
        intent.putExtra(WPWebViewActivity.URL_TO_LOAD, blog.getAdminUrl());
        intent.putExtra(WPWebViewActivity.AUTHENTICATION_URL, WPWebViewActivity.getBlogLoginUrl(blog));
        intent.putExtra(WPWebViewActivity.LOCAL_BLOG_ID, blog.getLocalTableBlogId());
        context.startActivity(intent);
    }

    public static void addNewBlogPostOrPage(Context context, Blog blog, boolean isPage) {
        // Create a new post object
        Post newPost = new Post(blog.getLocalTableBlogId(), isPage);
        WordPress.wpDB.savePost(newPost);

        Intent intent = new Intent(context, EditPostActivity.class);
        intent.putExtra(EditPostActivity.EXTRA_POSTID, newPost.getLocalTablePostId());
        intent.putExtra(EditPostActivity.EXTRA_IS_PAGE, isPage);
        intent.putExtra(EditPostActivity.EXTRA_IS_NEW_POST, true);
        context.startActivity(intent);
    }

    public static void editBlogPostorPage(Activity activity, long postOrPageId, boolean isPage) {
        Intent intent = new Intent(activity.getApplicationContext(), EditPostActivity.class);
        intent.putExtra(EditPostActivity.EXTRA_POSTID, postOrPageId);
        intent.putExtra(EditPostActivity.EXTRA_IS_PAGE, isPage);
        activity.startActivityForResult(intent, RequestCodes.EDIT_POST);
    }

    public static void addMedia(Context context, Blog blog) {
        // TODO: https://github.com/wordpress-mobile/WordPress-Android/issues/2394
    }

    public static void viewSSLCerts(Context context) {
        try {
            Intent intent = new Intent(context, SSLCertsViewActivity.class);
            SelfSignedSSLCertsManager selfSignedSSLCertsManager = SelfSignedSSLCertsManager.getInstance(context);
            String lastFailureChainDescription =
                    selfSignedSSLCertsManager.getLastFailureChainDescription().replaceAll("\n", "<br/>");
            intent.putExtra(SSLCertsViewActivity.CERT_DETAILS_KEYS, lastFailureChainDescription);
            context.startActivity(intent);
        } catch (GeneralSecurityException e) {
            AppLog.e(AppLog.T.API, e);
        } catch (IOException e) {
            AppLog.e(AppLog.T.API, e);
        }
    }

    public static void viewSettings(Activity activity) {
        Intent i = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(i, RequestCodes.SETTINGS);
    }

    public static void newAccount(Activity activity) {
        Intent intent = new Intent(activity, NewAccountActivity.class);
        activity.startActivityForResult(intent, SignInActivity.CREATE_ACCOUNT_REQUEST);
    }

    public static void viewStatsSinglePostDetails(Context context, PostModel post) {
        Intent statsPostViewIntent = new Intent(context, StatsSinglePostDetailsActivity.class);
        statsPostViewIntent.putExtra(StatsSinglePostDetailsActivity.ARG_REMOTE_POST_OBJECT, post);
        context.startActivity(statsPostViewIntent);
    }
}
