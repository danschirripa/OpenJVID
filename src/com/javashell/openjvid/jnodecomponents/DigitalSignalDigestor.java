package com.javashell.openjvid.jnodecomponents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;

import org.jaudiolibs.jnajack.JackClient;

import com.javashell.audio.AudioProcessor;
import com.javashell.audio.JackAudioDigestor;
import com.javashell.audio.dsp.DigitalSignalProcessor;
import com.javashell.video.VideoProcessor;

public class DigitalSignalDigestor implements VideoProcessor, AudioProcessor {
	private final DigitalSignalProcessor dsp;
	private JackAudioDigestor audioClient;

	private HashSet<AudioProcessor> audioProcessors = new HashSet<AudioProcessor>();

	private float delay, decayFactor;

	public DigitalSignalDigestor(DigitalSignalProcessor dsp, float delay, float decayFactor) {
		this.dsp = dsp;
		this.delay = delay;
		this.decayFactor = decayFactor;
		audioClient = new JackAudioDigestor("DSP", null, null) {

			@Override
			public boolean process(JackClient client, int nframes) {
				return true;
			}

		};
	}

	@Override
	public void processSamples(AudioProcessor p, FloatBuffer[] samples) {
		if (samples == null)
			return;
		for (int i = 0; i < samples.length; i++)
			samples[i] = dsp.processSamples(samples[i], delay, decayFactor, audioClient.getSampleRate());

		for (AudioProcessor ap : audioProcessors) {
			ap.processSamples(this, samples);
		}
	}

	@Override
	public int getAudioChannels() {
		return 1;
	}

	@Override
	public void addSubscriber(AudioProcessor p, int localInputChannel, int originOutputChannel) {
		audioProcessors.add(p);
	}

	@Override
	public void removeSubscriber(AudioProcessor p, int localInputChannel, int originOutputChannel) {
		audioProcessors.remove(p);
	}

	@Override
	public BufferedImage processFrame(BufferedImage frame) {
		return frame;
	}

	@Override
	public boolean open() {
		return true;
	}

	@Override
	public boolean close() {
		return true;
	}

	@Override
	public Dimension getResolution() {
		return null;
	}

	@Override
	public void addSubscription(AudioProcessor origin, int localInputChannel, int originOutputChannel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSubscription(AudioProcessor origin, int localInputChannel, int originOutputChannel) {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap<String, Object> getPropertyTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPropertyTable(HashMap<String, Object> table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
