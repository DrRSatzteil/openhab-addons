/**
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.mqtt.awtrixlight.internal.handler;

import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_ACTIVE;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_AUTOSCALE;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_BACKGROUND;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_BAR;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_BLINK_TEXT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_BUTLEFT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_BUTRIGHT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_BUTSELECT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_CENTER;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_COLOR;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_DURATION;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_EFFECT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_EFFECT_BLEND;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_EFFECT_PALETTE;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_EFFECT_SPEED;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_FADE_TEXT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_GRADIENT_COLOR;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_ICON;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_LIFETIME;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_LIFETIME_MODE;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_LINE;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_OVERLAY;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_PROGRESS;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_PROGRESSBC;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_PROGRESSC;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_PUSH_ICON;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_RAINBOW;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_RESET;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_SCROLLSPEED;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_TEXT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_TEXTCASE;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_TEXT_OFFSET;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.CHANNEL_TOP_TEXT;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.PROP_APPID;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.PROP_UNIQUEID;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.PUSH_ICON_OPTION_0;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.PUSH_ICON_OPTION_1;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.PUSH_ICON_OPTION_2;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.THING_TYPE_APP;
import static org.openhab.binding.mqtt.awtrixlight.internal.AwtrixLightBindingConstants.THOUSAND;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mqtt.awtrixlight.internal.AppConfigOptions;
import org.openhab.binding.mqtt.awtrixlight.internal.Helper;
import org.openhab.binding.mqtt.awtrixlight.internal.app.AwtrixApp;
import org.openhab.core.io.transport.mqtt.MqttMessageSubscriber;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AwtrixLightAppHandler} is responsible for handling commands for an app and will send mqtt messages to
 * update the app configuration on the awtrix clock. It will also emit trigger events as long as the app is locked on
 * the clock.
 *
 * @author Thomas Lauterbach - Initial contribution
 */
@NonNullByDefault
public class AwtrixLightAppHandler extends BaseThingHandler implements MqttMessageSubscriber {

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Set.of(THING_TYPE_APP);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String channelPrefix = "";
    private String appName = "";

    private Boolean synchronizationRequired = true;
    private Boolean buttonControlled = false;
    private Boolean active = true;

    private AwtrixApp app = new AwtrixApp();

    private @Nullable ScheduledFuture<?> finishInitJob;

    public AwtrixLightAppHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.trace("Received command {} of type {} on channel {}", command.toString(), command.getClass(),
                channelUID.getAsString());
        if (this.synchronizationRequired) {
            // Don't accept any commands while we're synchronizing our settings
            return;
        }

        if (command instanceof RefreshType) {
            updateApp();
            return;
        }
        switch (channelUID.getId()) {
            case CHANNEL_ACTIVE:
                // WARNING: Inactive Apps wont survive an OH reboot
                if (command instanceof OnOffType) {
                    if (OnOffType.OFF.equals(command)) {
                        this.active = false;
                        deleteApp();
                    } else if (OnOffType.ON.equals(command)) {
                        this.active = true;
                    }
                }
                break;
            case CHANNEL_RESET:
                if (command instanceof OnOffType) {
                    if (OnOffType.ON.equals((OnOffType) command)) {
                        deleteApp();
                        this.app = new AwtrixApp();
                        updateApp();
                        initStates();
                        return;
                    }
                }
                break;
            case CHANNEL_COLOR:
                if (command instanceof HSBType) {
                    int[] hsbToRgb = ColorUtil.hsbToRgb((HSBType) command);
                    this.app.setColor(convertRgbArray(hsbToRgb));
                }
                break;
            case CHANNEL_GRADIENT_COLOR:
                if (command instanceof HSBType) {
                    int[] hsbToRgb = ColorUtil.hsbToRgb((HSBType) command);
                    this.app.setGradient(convertRgbArray(hsbToRgb));
                }
                break;
            case CHANNEL_SCROLLSPEED:
                if (command instanceof QuantityType) {
                    this.app.setScrollSpeed(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_DURATION:
                if (command instanceof QuantityType) {
                    this.app.setDuration(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_EFFECT:
                if (command instanceof StringType) {
                    this.app.setEffect(((StringType) command).toString());
                }
                break;
            case CHANNEL_EFFECT_SPEED:
                if (command instanceof QuantityType) {
                    this.app.setEffectSpeed(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_EFFECT_PALETTE:
                if (command instanceof StringType) {
                    this.app.setEffectPalette(((StringType) command).toString());
                }
                break;
            case CHANNEL_EFFECT_BLEND:
                if (command instanceof OnOffType) {
                    this.app.setEffectBlend(command.equals(OnOffType.ON));
                }
                break;
            case CHANNEL_TEXT:
                if (command instanceof StringType) {
                    this.app.setText(((StringType) command).toString());
                }
                break;
            case CHANNEL_TEXT_OFFSET:
                if (command instanceof QuantityType) {
                    this.app.setTextOffset(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_TOP_TEXT:
                if (command instanceof OnOffType) {
                    this.app.setTopText(command.equals(OnOffType.ON));
                }
                break;
            case CHANNEL_TEXTCASE:
                if (command instanceof QuantityType) {
                    this.app.setTextCase(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_CENTER:
                if (command instanceof OnOffType) {
                    this.app.setCenter(command.equals(OnOffType.ON));
                }
                break;
            case CHANNEL_BLINK_TEXT:
                if (command instanceof QuantityType) {
                    QuantityType<?> blinkInS = ((QuantityType<?>) command).toUnit(Units.SECOND);
                    if (blinkInS != null) {
                        BigDecimal blinkInMs = blinkInS.toBigDecimal().multiply(THOUSAND);
                        this.app.setBlinkText(blinkInMs);
                    }
                }
                break;
            case CHANNEL_FADE_TEXT:
                if (command instanceof QuantityType) {
                    QuantityType<?> fadeInS = ((QuantityType<?>) command).toUnit(Units.SECOND);
                    if (fadeInS != null) {
                        BigDecimal fadeInMs = fadeInS.toBigDecimal().multiply(THOUSAND);
                        this.app.setFadeText(fadeInMs);
                    }
                }
                break;
            case CHANNEL_RAINBOW:
                if (command instanceof OnOffType) {
                    this.app.setRainbow(command.equals(OnOffType.ON));
                }
                break;
            case CHANNEL_ICON:
                if (command instanceof StringType) {
                    this.app.setIcon(((StringType) command).toString());
                }
                break;
            case CHANNEL_PUSH_ICON:
                if (command instanceof StringType) {
                    switch (((StringType) command).toString()) {
                        case PUSH_ICON_OPTION_0:
                            this.app.setPushIcon(new BigDecimal(0));
                            break;
                        case PUSH_ICON_OPTION_1:
                            this.app.setPushIcon(new BigDecimal(1));
                            break;
                        case PUSH_ICON_OPTION_2:
                            this.app.setPushIcon(new BigDecimal(2));
                            break;
                    }
                }
                break;
            case CHANNEL_BACKGROUND:
                if (command instanceof HSBType) {
                    int[] hsbToRgb = ColorUtil.hsbToRgb((HSBType) command);
                    this.app.setBackground(convertRgbArray(hsbToRgb));
                }
                break;
            case CHANNEL_LINE:
                if (command instanceof StringType) {
                    try {
                        String[] points = command.toString().split(",");
                        BigDecimal[] pointsAsNumber = new BigDecimal[points.length];
                        for (int i = 0; i < points.length; i++) {
                            pointsAsNumber[i] = new BigDecimal(points[i]);
                        }
                        this.app.setLine(pointsAsNumber);
                    } catch (Exception e) {
                        logger.warn("Command {} cannot be parsed as line graph. Format should be: 1,2,3,4,5",
                                command.toString());
                    }
                }
                break;
            case CHANNEL_LIFETIME:
                if (command instanceof QuantityType) {
                    this.app.setLifetime(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_LIFETIME_MODE:
                if (command instanceof StringType) {
                    switch (command.toString()) {
                        case "DELETE":
                            this.app.setLifetimeMode(new BigDecimal(0));
                            break;
                        case "STALE":
                            this.app.setLifetimeMode(new BigDecimal(1));
                            break;
                    }
                }
                break;
            case CHANNEL_BAR:
                if (command instanceof StringType) {
                    try {
                        String[] points = command.toString().split(",");
                        BigDecimal[] pointsAsNumber = new BigDecimal[points.length];
                        for (int i = 0; i < points.length; i++) {
                            pointsAsNumber[i] = new BigDecimal(points[i]);
                        }
                        this.app.setBar(pointsAsNumber);
                    } catch (Exception e) {
                        logger.warn("Command {} cannot be parsed as bar graph. Format should be: 1,2,3,4,5",
                                command.toString());
                    }
                }
                break;
            case CHANNEL_AUTOSCALE:
                if (command instanceof OnOffType) {
                    this.app.setAutoscale(command.equals(OnOffType.ON));
                }
                break;
            case CHANNEL_OVERLAY:
                if (command instanceof StringType) {
                    this.app.setOverlay(((StringType) command).toString());
                }
                break;
            case CHANNEL_PROGRESS:
                if (command instanceof QuantityType) {
                    this.app.setProgress(((QuantityType<?>) command).toBigDecimal());
                }
                break;
            case CHANNEL_PROGRESSC:
                if (command instanceof HSBType) {
                    int[] hsbToRgb = ColorUtil.hsbToRgb((HSBType) command);
                    this.app.setProgressC(convertRgbArray(hsbToRgb));
                }
                break;
            case CHANNEL_PROGRESSBC:
                if (command instanceof HSBType) {
                    int[] hsbToRgb = ColorUtil.hsbToRgb((HSBType) command);
                    this.app.setProgressBC(convertRgbArray(hsbToRgb));
                }
                break;
        }
        logger.debug("Current app configuration: {}", this.app.toString());
        if (this.active) {
            updateApp();
        }
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        switch (channelUID.getId()) {
            case CHANNEL_COLOR:
                this.app.setColor(AwtrixApp.DEFAULT_COLOR);
                break;
            case CHANNEL_GRADIENT_COLOR:
                this.app.setGradient(AwtrixApp.DEFAULT_GRADIENT);
                break;
            case CHANNEL_SCROLLSPEED:
                this.app.setScrollSpeed(AwtrixApp.DEFAULT_SCROLLSPEED);
                break;
            case CHANNEL_DURATION:
                this.app.setDuration(AwtrixApp.DEFAULT_DURATION);
                break;
            case CHANNEL_EFFECT:
                this.app.setEffect(AwtrixApp.DEFAULT_EFFECT);
                break;
            case CHANNEL_EFFECT_SPEED:
                this.app.setEffectSpeed(AwtrixApp.DEFAULT_EFFECTSPEED);
                break;
            case CHANNEL_EFFECT_PALETTE:
                this.app.setEffectPalette(AwtrixApp.DEFAULT_EFFECTPALETTE);
                break;
            case CHANNEL_EFFECT_BLEND:
                this.app.setEffectBlend(AwtrixApp.DEFAULT_EFFECTBLEND);
                break;
            case CHANNEL_TEXT:
                this.app.setText(AwtrixApp.DEFAULT_TEXT);
                break;
            case CHANNEL_TEXT_OFFSET:
                this.app.setTextOffset(AwtrixApp.DEFAULT_TEXTOFFSET);
                break;
            case CHANNEL_TOP_TEXT:
                this.app.setTopText(AwtrixApp.DEFAULT_TOPTEXT);
                break;
            case CHANNEL_TEXTCASE:
                this.app.setTextCase(AwtrixApp.DEFAULT_TEXTCASE);
                break;
            case CHANNEL_CENTER:
                this.app.setCenter(AwtrixApp.DEFAULT_CENTER);
                break;
            case CHANNEL_BLINK_TEXT:
                this.app.setBlinkText(AwtrixApp.DEFAULT_BLINKTEXT);
                break;
            case CHANNEL_FADE_TEXT:
                this.app.setFadeText(AwtrixApp.DEFAULT_FADETEXT);
                break;
            case CHANNEL_RAINBOW:
                this.app.setRainbow(AwtrixApp.DEFAULT_RAINBOW);
                break;
            case CHANNEL_ICON:
                this.app.setIcon(AwtrixApp.DEFAULT_ICON);
                break;
            case CHANNEL_PUSH_ICON:
                this.app.setPushIcon(AwtrixApp.DEFAULT_PUSHICON);
                break;
            case CHANNEL_BACKGROUND:
                this.app.setBackground(AwtrixApp.DEFAULT_BACKGROUND);
                break;
            case CHANNEL_LINE:
                this.app.setLine(AwtrixApp.DEFAULT_LINE);
                break;
            case CHANNEL_LIFETIME:
                this.app.setLifetime(AwtrixApp.DEFAULT_LIFETIME);
                break;
            case CHANNEL_LIFETIME_MODE:
                this.app.setLifetimeMode(AwtrixApp.DEFAULT_LIFETIME_MODE);
                break;
            case CHANNEL_BAR:
                this.app.setBar(AwtrixApp.DEFAULT_BAR);
                break;
            case CHANNEL_AUTOSCALE:
                this.app.setAutoscale(AwtrixApp.DEFAULT_AUTOSCALE);
                break;
            case CHANNEL_OVERLAY:
                this.app.setOverlay(AwtrixApp.DEFAULT_OVERLAY);
                break;
            case CHANNEL_PROGRESS:
                this.app.setProgress(AwtrixApp.DEFAULT_PROGRESS);
                break;
            case CHANNEL_PROGRESSC:
                this.app.setProgressC(AwtrixApp.DEFAULT_PROGRESSC);
                break;
            case CHANNEL_PROGRESSBC:
                this.app.setProgressBC(AwtrixApp.DEFAULT_PROGRESSBC);
                break;
        }
        logger.debug("Current app configuration: {}", this.app.toString());
        updateApp();
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        // One might consider not updating all unaffected channels but it does not really hurt
        initStates();
    }

    @Override
    public void dispose() {
        Future<?> localFinishJob = this.finishInitJob;
        if (localFinishJob != null && !localFinishJob.isCancelled() && !localFinishJob.isDone()) {
            localFinishJob.cancel(true);
        }
    }

    @Override
    public void handleRemoval() {
        deleteApp();
        updateStatus(ThingStatus.REMOVED);
    }

    @Override
    public void initialize() {
        AppConfigOptions config = getConfigAs(AppConfigOptions.class);
        this.appName = config.appname;
        this.buttonControlled = config.useButtons;
        this.channelPrefix = getThing().getUID() + ":";
        thing.setProperty(PROP_APPID, this.appName);
        logger.trace("Configured handler for app {} with channelPrefix {}", this.appName, this.channelPrefix);
        bridgeStatusChanged(getBridgeStatus());
    }

    public ThingStatusInfo getBridgeStatus() {
        Bridge b = getBridge();
        if (b != null) {
            return b.getStatusInfo();
        } else {
            return new ThingStatusInfo(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE, null);
        }
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.OFFLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            return;
        }
        if (bridgeStatusInfo.getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR);
            return;
        }

        Bridge localBridge = this.getBridge();
        if (localBridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_UNINITIALIZED, "Bridge is missing or offline.");
            return;
        }
        ThingHandler handler = localBridge.getHandler();
        if (handler instanceof AwtrixLightBridgeHandler) {
            AwtrixLightBridgeHandler albh = (AwtrixLightBridgeHandler) handler;
            Map<String, String> bridgeProperties = albh.getThing().getProperties();
            String bridgeHardwareId = bridgeProperties.get(PROP_UNIQUEID);
            if (bridgeHardwareId != null) {
                thing.setProperty(PROP_APPID, bridgeHardwareId + "-" + this.appName);
            }
            if (this.synchronizationRequired) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NOT_YET_READY, "Synchronizing...");
                this.finishInitJob = scheduler.schedule(this::finishInit, 15, TimeUnit.SECONDS);
            } else {
                finishInit();
            }
        }
    }

    @Override
    public void processMessage(String topic, byte[] payload) {
        synchronized (this.synchronizationRequired) {
            if (this.synchronizationRequired) {
                this.synchronizationRequired = false;
                String payloadString = new String(payload, StandardCharsets.UTF_8);
                HashMap<String, Object> decodedJson = Helper.decodeJson(payloadString);
                this.app.updateFields(decodedJson);
                initStates();
                finishInit();
            }
        }
    }

    public String getAppName() {
        return this.appName;
    }

    public boolean isButtonControlled() {
        return this.buttonControlled;
    }

    void handleLeftButton(String event) {
        triggerChannel(new ChannelUID(channelPrefix + CHANNEL_BUTLEFT), event);
    }

    void handleRightButton(String event) {
        triggerChannel(new ChannelUID(channelPrefix + CHANNEL_BUTRIGHT), event);
    }

    void handleSelectButton(String event) {
        triggerChannel(new ChannelUID(channelPrefix + CHANNEL_BUTSELECT), event);
    }

    private BigDecimal[] convertRgbArray(int[] rgbIn) {
        if (rgbIn.length == 3) {
            BigDecimal[] rgb = new BigDecimal[3];
            rgb[0] = new BigDecimal(rgbIn[0]);
            rgb[1] = new BigDecimal(rgbIn[1]);
            rgb[2] = new BigDecimal(rgbIn[2]);
            return rgb;
        } else {
            return new BigDecimal[0];
        }
    }

    private void deleteApp() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler bridgeHandler = bridge.getHandler();
            if (bridgeHandler instanceof AwtrixLightBridgeHandler) {
                AwtrixLightBridgeHandler albh = (AwtrixLightBridgeHandler) bridgeHandler;
                albh.deleteApp(this.appName);
            }
        }
    }

    private void updateApp() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler bridgeHandler = bridge.getHandler();
            if (bridgeHandler instanceof AwtrixLightBridgeHandler) {
                AwtrixLightBridgeHandler albh = (AwtrixLightBridgeHandler) bridgeHandler;
                albh.updateApp(this.appName, this.app.getAppConfig());
            }
        }
    }

    private void initStates() {
        updateState(new ChannelUID(channelPrefix + CHANNEL_ACTIVE), this.active ? OnOffType.ON : OnOffType.OFF);

        BigDecimal[] color = this.app.getColor().length == 3 ? this.app.getColor()
                : new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        updateState(new ChannelUID(channelPrefix + CHANNEL_COLOR),
                HSBType.fromRGB(color[0].intValue(), color[1].intValue(), color[2].intValue()));

        BigDecimal[] gradient = this.app.getGradient().length == 3 ? this.app.getGradient()
                : new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        updateState(new ChannelUID(channelPrefix + CHANNEL_GRADIENT_COLOR),
                HSBType.fromRGB(gradient[0].intValue(), gradient[1].intValue(), gradient[2].intValue()));

        updateState(new ChannelUID(channelPrefix + CHANNEL_SCROLLSPEED),
                new QuantityType<>(this.app.getScrollSpeed(), Units.PERCENT));

        updateState(new ChannelUID(channelPrefix + CHANNEL_DURATION),
                new QuantityType<>(this.app.getDuration(), Units.SECOND));

        updateState(new ChannelUID(channelPrefix + CHANNEL_EFFECT), new StringType(this.app.getEffect()));

        updateState(new ChannelUID(channelPrefix + CHANNEL_EFFECT_SPEED),
                new QuantityType<>(this.app.getEffectSpeed(), Units.ONE));

        updateState(new ChannelUID(channelPrefix + CHANNEL_EFFECT_PALETTE),
                new StringType(this.app.getEffectPalette()));

        updateState(new ChannelUID(channelPrefix + CHANNEL_EFFECT_BLEND),
                this.app.getEffectBlend() ? OnOffType.ON : OnOffType.OFF);

        updateState(new ChannelUID(channelPrefix + CHANNEL_TEXT), new StringType(this.app.getText()));

        updateState(new ChannelUID(channelPrefix + CHANNEL_TEXT_OFFSET),
                new QuantityType<>(this.app.getTextOffset(), Units.ONE));

        updateState(new ChannelUID(channelPrefix + CHANNEL_TOP_TEXT),
                this.app.getTopText() ? OnOffType.ON : OnOffType.OFF);

        updateState(new ChannelUID(channelPrefix + CHANNEL_CENTER),
                this.app.getCenter() ? OnOffType.ON : OnOffType.OFF);

        BigDecimal blinkTextInSeconds = this.app.getBlinkText().divide(THOUSAND);
        if (blinkTextInSeconds != null) {
            updateState(new ChannelUID(channelPrefix + CHANNEL_BLINK_TEXT),
                    new QuantityType<>(blinkTextInSeconds, Units.SECOND));
        }

        BigDecimal fadeTextInSeconds = this.app.getFadeText().divide(THOUSAND);
        if (fadeTextInSeconds != null) {
            updateState(new ChannelUID(channelPrefix + CHANNEL_FADE_TEXT),
                    new QuantityType<>(fadeTextInSeconds, Units.SECOND));
        }
        updateState(new ChannelUID(channelPrefix + CHANNEL_RAINBOW),
                this.app.getRainbow() ? OnOffType.ON : OnOffType.OFF);

        updateState(new ChannelUID(channelPrefix + CHANNEL_ICON), new StringType(this.app.getIcon()));

        String param = "";
        if (this.app.getPushIcon().equals(BigDecimal.ZERO)) {
            param = PUSH_ICON_OPTION_0;
        } else if (this.app.getPushIcon().equals(BigDecimal.ONE)) {
            param = PUSH_ICON_OPTION_1;
        } else if (this.app.getPushIcon().equals(new BigDecimal(2))) {
            param = PUSH_ICON_OPTION_2;
        }
        updateState(new ChannelUID(channelPrefix + CHANNEL_PUSH_ICON), new StringType(param));

        updateState(new ChannelUID(channelPrefix + CHANNEL_TEXTCASE),
                new QuantityType<>(this.app.getBlinkText(), Units.ONE));

        BigDecimal[] background = this.app.getBackground().length == 3 ? this.app.getBackground()
                : new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        updateState(new ChannelUID(channelPrefix + CHANNEL_BACKGROUND),
                HSBType.fromRGB(background[0].intValue(), background[1].intValue(), background[2].intValue()));

        BigDecimal[] line = this.app.getLine().length > 0 ? this.app.getLine() : new BigDecimal[] { BigDecimal.ZERO };
        String lineString = Arrays.stream(line).map(BigDecimal::toString).collect(Collectors.joining(","));
        updateState(new ChannelUID(channelPrefix + CHANNEL_LINE), new StringType(lineString));

        updateState(new ChannelUID(channelPrefix + CHANNEL_LIFETIME),
                new QuantityType<>(this.app.getLifetime(), Units.SECOND));

        String lifetimeMode = this.app.getLifetimeMode().equals(BigDecimal.ZERO) ? "DELETE" : "STALE";
        updateState(new ChannelUID(channelPrefix + CHANNEL_LIFETIME_MODE), new StringType(lifetimeMode));

        BigDecimal[] bar = this.app.getBar().length > 0 ? this.app.getBar() : new BigDecimal[] { BigDecimal.ZERO };
        String barString = Arrays.stream(bar).map(BigDecimal::toString).collect(Collectors.joining(","));
        updateState(new ChannelUID(channelPrefix + CHANNEL_BAR), new StringType(barString));

        updateState(new ChannelUID(channelPrefix + CHANNEL_AUTOSCALE),
                this.app.getAutoscale() ? OnOffType.ON : OnOffType.OFF);

        updateState(new ChannelUID(channelPrefix + CHANNEL_OVERLAY), new StringType(this.app.getOverlay()));

        BigDecimal progress = this.app.getProgress().compareTo(BigDecimal.ZERO) > 0 ? this.app.getProgress()
                : BigDecimal.ZERO;
        updateState(new ChannelUID(channelPrefix + CHANNEL_PROGRESS), new QuantityType<>(progress, Units.PERCENT));

        BigDecimal[] progressC = this.app.getProgressC().length == 3 ? this.app.getProgressC()
                : new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        updateState(new ChannelUID(channelPrefix + CHANNEL_PROGRESSC),
                HSBType.fromRGB(progressC[0].intValue(), progressC[1].intValue(), progressC[2].intValue()));

        BigDecimal[] progressBC = this.app.getProgressBC().length == 3 ? this.app.getProgressBC()
                : new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
        updateState(new ChannelUID(channelPrefix + CHANNEL_PROGRESSBC),
                HSBType.fromRGB(progressBC[0].intValue(), progressBC[1].intValue(), progressBC[2].intValue()));
    }

    private void finishInit() {
        synchronized (this.synchronizationRequired) {
            if (this.synchronizationRequired) {
                this.synchronizationRequired = false;
                initStates();
                updateApp();
            }
        }
        updateStatus(ThingStatus.ONLINE, ThingStatusDetail.NONE);
        Future<?> localJob = this.finishInitJob;
        if (localJob != null && !localJob.isCancelled() && !localJob.isDone()) {
            localJob.cancel(true);
            this.finishInitJob = null;
        }
    }

    public void setActive(boolean active) {
        if (this.active != active) {
            this.active = active;
            updateState(new ChannelUID(channelPrefix + CHANNEL_ACTIVE), active ? OnOffType.ON : OnOffType.OFF);
        }
    }
}
