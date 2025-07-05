package com.applovin.mediation.adapters;

import android.app.Activity;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.MaxSignalProvider;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxNativeAdAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxSignalCollectionListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterSignalCollectionParameters;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.lunamedia.lunasdk.LunaSDK;
import io.lunamedia.lunasdk.SdkInitializationListener;
import io.lunamedia.lunasdk.ads.AdMetaInfo;
import io.lunamedia.lunasdk.ads.LunaMediaAdRequestStatus;
import io.lunamedia.lunasdk.ads.LunaMediaBanner;
import io.lunamedia.lunasdk.ads.LunaMediaInterstitial;
import io.lunamedia.lunasdk.ads.LunaMediaNative;
import io.lunamedia.lunasdk.ads.LunaMediaRewarded;
import io.lunamedia.lunasdk.listeners.BannerAdEventListener;

@Keep
public class LunaMediaMediationAdapter
        extends MediationAdapterBase
        implements MaxSignalProvider, MaxInterstitialAdapter, /* MaxAppOpenAdapter */ MaxRewardedAdapter, MaxAdViewAdapter /* MaxNativeAdAdapter */
{
    private static final AtomicBoolean        initialized = new AtomicBoolean();
    private static       InitializationStatus initializationStatus;

    private LunaMediaBanner adViewAd;
    private LunaMediaInterstitial   interstitialAd;
    private LunaMediaRewarded rewardedAd;
    private LunaMediaNative nativeAd;
    private LunaMediaInterstitial   appOpenAd;

    // Explicit default constructor declaration
    public LunaMediaMediationAdapter(final AppLovinSdk sdk) { super( sdk ); }

    @Override
    public void initialize(final MaxAdapterInitializationParameters parameters, @Nullable final Activity activity, final OnCompletionListener onCompletionListener)
    {

        if ( initialized.compareAndSet( false, true ) )
        {
            String appId = parameters.getServerParameters().getString( "app_id", null );

            initializationStatus = InitializationStatus.INITIALIZING;

            LunaSDK.INSTANCE.init(getApplicationContext(), new SdkInitializationListener() {
                @Override
                public void onInitializationComplete(Error error) {
                    if (error != null) {
                        log("LunaMedia SDK initialization failed with error: " + error.getMessage());

                        initializationStatus = MaxAdapter.InitializationStatus.INITIALIZED_FAILURE;
                        onCompletionListener.onCompletion(initializationStatus, error.getMessage());
                    } else {
                        log("LunaMedia SDK successfully initialized.");

                        initializationStatus = MaxAdapter.InitializationStatus.INITIALIZED_SUCCESS;
                        onCompletionListener.onCompletion(initializationStatus, null);
                    }
                }
            });
        }
        else
        {
            onCompletionListener.onCompletion( initializationStatus, null );
        }
    }

    @Override
    public String getSdkVersion()
    {
        return LunaSDK.INSTANCE.getVersion();
    }

    @Override
    public String getAdapterVersion()
    {
        return "1.0.0";
    }

    @Override
    public void onDestroy()
    {
        if ( adViewAd != null )
        {
            adViewAd = null;
        }

        if ( nativeAd != null )
        {
            nativeAd = null;
        }

        if ( interstitialAd != null )
        {
            interstitialAd = null;
        }

        if ( rewardedAd != null )
        {
            rewardedAd = null;
        }

        if ( appOpenAd != null )
        {
            appOpenAd = null;
        }
    }

    //region Signal Collection

    @Override
    public void collectSignal(final MaxAdapterSignalCollectionParameters parameters, @Nullable final Activity activity, final MaxSignalCollectionListener callback)
    {
        log( "Collecting signal..." );
        // need to update bid token from sdk
//        callback.onSignalCollected( "BID_TOKEN" );
    }


    @Override
    public void loadInterstitialAd(final MaxAdapterResponseParameters parameters, @Nullable final Activity activity, final MaxInterstitialAdapterListener listener)
    {
        String bidResponse = parameters.getBidResponse();
        boolean isBiddingAd = AppLovinSdkUtils.isValidString( bidResponse );
        String placementId = parameters.getThirdPartyAdPlacementId();
        log( "Loading " + ( isBiddingAd ? "bidding " : "" ) + "interstitial ad for placement: " + placementId + "..." );

//        if ( shouldFailAdLoadWhenSdkNotInitialized( parameters ) && !LunaSDK.INSTANCE.isSDKInitialized() )
//        {
//            log( "Luna Media SDK not successfully initialized: failing interstitial ad load..." );
//            listener.onInterstitialAdLoadFailed( MaxAdapterError.NOT_INITIALIZED );
//
//            return;
//        }
//
//        updateUserPrivacySettings( parameters );

//        interstitialAd = new LunaMediaInterstitial( getContext( activity ), placementId, new AdConfig() );
//        interstitialAd.setAdListener( new InterstitialListener( listener ) );

//        interstitialAd.load( bidResponse );
    }

    @Override
    public void showInterstitialAd(final MaxAdapterResponseParameters parameters, @Nullable final Activity activity, final MaxInterstitialAdapterListener listener)
    {
//         && interstitialAd.canPlayAd()
        if ( interstitialAd != null )
        {
            log( "Showing interstitial ad for placement: " + parameters.getThirdPartyAdPlacementId() + "..." );
//            interstitialAd.play( getContext( activity ) );
        }
        else
        {
            log( "Interstitial ad is not ready: " + parameters.getThirdPartyAdPlacementId() + "..." );
            listener.onInterstitialAdDisplayFailed( new MaxAdapterError( MaxAdapterError.AD_DISPLAY_FAILED, 0, "Interstitial ad is not ready" ) );
        }
    }

    @Override
    public void loadRewardedAd(final MaxAdapterResponseParameters parameters, @Nullable final Activity activity, final MaxRewardedAdapterListener listener)
    {
        String bidResponse = parameters.getBidResponse();
        boolean isBiddingAd = AppLovinSdkUtils.isValidString( bidResponse );
        String placementId = parameters.getThirdPartyAdPlacementId();
        log( "Loading " + ( isBiddingAd ? "bidding " : "" ) + "rewarded ad for placement: " + placementId + "..." );

//        if ( shouldFailAdLoadWhenSdkNotInitialized( parameters ) && !LunaSDK.INSTANCE.isSDKInitialized())
//        {
//            log( "Luna Media SDK not successfully initialized: failing rewarded ad load..." );
//            listener.onRewardedAdLoadFailed( MaxAdapterError.NOT_INITIALIZED );
//
//            return;
//        }

//        updateUserPrivacySettings( parameters );

//        rewardedAd = new RewardedAd( getContext( activity ), placementId, new AdConfig() );
//        rewardedAd.setAdListener( new RewardedListener( listener ) );
//
//        rewardedAd.load( bidResponse );
    }

    @Override
    public void showRewardedAd(final MaxAdapterResponseParameters parameters, @Nullable final Activity activity, final MaxRewardedAdapterListener listener)
    {
//        && rewardedAd.canPlayAd()
        if ( rewardedAd != null  )
        {
            log( "Showing rewarded ad for placement: " + parameters.getThirdPartyAdPlacementId() + "..." );

            configureReward( parameters );
//            rewardedAd.play( getContext( activity ) );
        }
        else
        {
            log( "Rewarded ad is not ready: " + parameters.getThirdPartyAdPlacementId() + "..." );
            listener.onRewardedAdDisplayFailed( new MaxAdapterError( MaxAdapterError.AD_DISPLAY_FAILED, 0, "Rewarded ad is not ready" ) );
        }
    }

    //endregion

    //region MaxAdViewAdapter

    @Override
    public void loadAdViewAd(final MaxAdapterResponseParameters parameters, final MaxAdFormat adFormat, @Nullable final Activity activity, final MaxAdViewAdapterListener listener)
    {
        final String bidResponse = parameters.getBidResponse();
        final String adFormatLabel = adFormat.getLabel();
        final String placementId = parameters.getThirdPartyAdPlacementId();
//        final Context context = getContext( activity );

        final boolean isBiddingAd = AppLovinSdkUtils.isValidString( bidResponse );
        final boolean isNative = parameters.getServerParameters().getBoolean( "is_native" );

        log( "Loading " + ( isBiddingAd ? "bidding " : "" ) + ( isNative ? "native " : "" ) + adFormatLabel + " ad for placement: " + placementId + "..." );

        if ( shouldFailAdLoadWhenSdkNotInitialized( parameters ) && !LunaSDK.INSTANCE.isSDKInitialized())
        {
            log( "Luna Media SDK not successfully initialized: failing " + adFormatLabel + " ad load..." );
            listener.onAdViewAdLoadFailed( MaxAdapterError.NOT_INITIALIZED );

            return;
        }

//        updateUserPrivacySettings( parameters );

        if ( isNative )
        {
//            final NativeAdViewListener nativeAdViewListener = new NativeAdViewListener( parameters, adFormat, context, listener );
//            nativeAd = new NativeAd( getContext( activity ), placementId );
//            nativeAd.setAdListener( nativeAdViewListener );
//
//            nativeAd.load( bidResponse );

            return;
        }

        // Check if adaptive ad view sizes should be used
//        boolean isAdaptiveAdViewEnabled = isAdaptiveAdViewEnabled( parameters );
//        if ( isAdaptiveAdViewEnabled && AppLovinSdk.VERSION_CODE < 13_02_00_99 )
//        {
//            isAdaptiveAdViewEnabled = false;
//            userError( "Please update AppLovin MAX SDK to version 13.2.0 or higher in order to use Luna Media adaptive ads" );
//        }

        adViewAd = new LunaMediaBanner(activity, null);
        adViewAd.setListener(new BannerAdEventListener() {

            @Override
            public void onAdLoadSucceeded(LunaMediaBanner ad, AdMetaInfo metaInfo) {
                super.onAdLoadSucceeded(ad, metaInfo);
                listener.onAdViewAdLoaded( adViewAd );
            }

            @Override
            public void onAdLoadFailed(LunaMediaBanner ad, LunaMediaAdRequestStatus requestStatus) {
                super.onAdLoadFailed(ad, requestStatus);
                MaxAdapterError adapterError = MaxAdapterError.INTERNAL_ERROR;
//                MaxAdapterError adapterError = toMaxError( error );
                listener.onAdViewAdLoadFailed( adapterError );
            }

            @Override
            public void onAdClicked(LunaMediaBanner ad, Map<?, ?> data){
                super.onAdClicked(ad, (Map<Object, ?>) data);
                listener.onAdViewAdClicked();
            }
//            @Override
//            public void onAdClicked(LunaMediaBanner ad, Map<?, ?> data) {
//                super.onAdClicked(ad, data);
//                System.out.println("Banner Ad Clicked!");
//            }

            @Override
            public void onAdDisplayed(LunaMediaBanner ad) {
                super.onAdDisplayed(ad);
                listener.onAdViewAdDisplayed();
                System.out.println("Banner Ad Displayed!");
            }

            @Override
            public void onAdDismissed(LunaMediaBanner ad) {
                super.onAdDismissed(ad);
            }

            @Override
            public void onUserLeftApplication(LunaMediaBanner ad) {
                super.onUserLeftApplication(ad);
            }

            @Override
            public void onAdImpression(LunaMediaBanner ad) {
                super.onAdImpression(ad);
            }

            @Override
            public void onAdFetchFailed(LunaMediaBanner ad, LunaMediaAdRequestStatus requestStatus) {
                super.onAdFetchFailed(ad, requestStatus);
                // System.out.println("Banner Ad Fetch Failed: " + requestStatus.getMessage());
            }
        });
//        adViewAd.setAdListener( new AdViewAdListener( adFormatLabel, listener ) );

        adViewAd.load( "test" );
    }


    @Override
    public void loadNativeAd(final MaxAdapterResponseParameters parameters, @Nullable final Activity activity, final MaxNativeAdAdapterListener listener)
    {
        String bidResponse = parameters.getBidResponse();
        boolean isBiddingAd = AppLovinSdkUtils.isValidString( bidResponse );
        String placementId = parameters.getThirdPartyAdPlacementId();
        log( "Loading " + ( isBiddingAd ? "bidding " : "" ) + "native ad for placement: " + placementId + "..." );

//        if ( shouldFailAdLoadWhenSdkNotInitialized( parameters ) && !LunaSDK.INSTANCE.isSDKInitialized())
//        {
//            log( "Luna Media SDK not successfully initialized: failing interstitial ad load..." );
//            listener.onNativeAdLoadFailed( MaxAdapterError.NOT_INITIALIZED );
//
//            return;
//        }
//
//        updateUserPrivacySettings( parameters );

//        nativeAd = new NativeAd( getContext( activity ), placementId );
//        nativeAd.setAdListener( new NativeListener( parameters, getContext( activity ), listener ) );
//
//        nativeAd.load( bidResponse );
    }

    private boolean shouldFailAdLoadWhenSdkNotInitialized(final MaxAdapterResponseParameters parameters)
    {
        return parameters.getServerParameters().getBoolean( "fail_ad_load_when_sdk_not_initialized", true );
    }
}