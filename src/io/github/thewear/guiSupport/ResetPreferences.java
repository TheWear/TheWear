package io.github.thewear.guiSupport;

import java.util.ArrayList;
import java.util.List;

import io.github.thewear.thewearandroid.R;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class ResetPreferences extends DialogPreference {

	/**
	 * ResetPreferences resets all preferences given in the keysToReset. This
	 * class has an observable component that can be used to also visually reset
	 * the preferences inside the same PreferenceActivity.
	 * 
	 * @attr ref R.styleable#PreferenceKeys_keysToReset
	 */

	private CharSequence[] preferenceKeys;

	/**
	 * constructor ResetPreferences(Context context, AttributeSet attrs) gets
	 * the attributes
	 * 
	 * @param context
	 * @param attrs
	 */

	public ResetPreferences(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PreferenceKeys, 0, 0);

		preferenceKeys = a.getTextArray(R.styleable.PreferenceKeys_keysToReset);
		if (preferenceKeys == null) {
			throw new IllegalArgumentException(
					"IntegerListPreference: error - entryList is not specified");
		}
		a.recycle();
	}

	/**
	 * onDialogClosed(boolean positiveResult) clears the SharedPreferences for
	 * the given preferenceKeys
	 */

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "OK", persist the new value
		if (positiveResult) {
			int length = preferenceKeys.length;
			String[] stringPreferenceKeys = new String[length];
			Editor editor = getEditor();
			for (int i = 0; i < preferenceKeys.length; i++) {
				stringPreferenceKeys[i] = preferenceKeys[i].toString();
				editor.remove(stringPreferenceKeys[i]);
			}
			editor.commit();
			setChanged();
			notifyObservers(stringPreferenceKeys);
		}
	}

	/*
	 * Licence applies to code below
	 * 
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 * contributor license agreements. See the NOTICE file distributed with this
	 * work for additional information regarding copyright ownership. The ASF
	 * licenses this file to You under the Apache License, Version 2.0 (the
	 * "License"); you may not use this file except in compliance with the
	 * License. You may obtain a copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */

	/**
	 * Observable is used to notify a group of Observer objects when a change
	 * occurs. On creation, the set of observers is empty. After a change
	 * occurred, the application can call the {@link #notifyObservers()} method.
	 * This will cause the invocation of the {@code update()} method of all
	 * registered Observers. The order of invocation is not specified. This
	 * implementation will call the Observers in the order they registered.
	 * Subclasses are completely free in what order they call the update
	 * methods.
	 * 
	 * @see Observer
	 */
	List<PreferencesResetObserver> observers = new ArrayList<PreferencesResetObserver>();

	boolean changed = false;

	/**
	 * Adds the specified observer to the list of observers. If it is already
	 * registered, it is not added a second time.
	 * 
	 * @param observer
	 *            the Observer to add.
	 */
	public void addObserver(PreferencesResetObserver observer) {
		if (observer == null) {
			throw new NullPointerException("observer == null");
		}
		synchronized (this) {
			if (!observers.contains(observer))
				observers.add(observer);
		}
	}

	/**
	 * Clears the changed flag for this {@code Observable}. After calling
	 * {@code clearChanged()}, {@code hasChanged()} will return {@code false}.
	 */
	protected void clearChanged() {
		changed = false;
	}

	/**
	 * Returns the number of observers registered to this {@code Observable}.
	 * 
	 * @return the number of observers.
	 */
	public int countObservers() {
		return observers.size();
	}

	/**
	 * Removes the specified observer from the list of observers. Passing null
	 * won't do anything.
	 * 
	 * @param observer
	 *            the observer to remove.
	 */
	public synchronized void deleteObserver(PreferencesResetObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Removes all observers from the list of observers.
	 */
	public synchronized void deleteObservers() {
		observers.clear();
	}

	/**
	 * Returns the changed flag for this {@code Observable}.
	 * 
	 * @return {@code true} when the changed flag for this {@code Observable} is
	 *         set, {@code false} otherwise.
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
	 * method for every observer in the list of observers using null as the
	 * argument. Afterwards, calls {@code clearChanged()}.
	 * <p>
	 * Equivalent to calling {@code notifyObservers(null)}.
	 */
	public void notifyObservers() {
		notifyObservers(null);
	}

	/**
	 * If {@code hasChanged()} returns {@code true}, calls the {@code update()}
	 * method for every Observer in the list of observers using the specified
	 * argument. Afterwards calls {@code clearChanged()}.
	 * 
	 * @param data
	 *            the argument passed to {@code update()}.
	 */
	public void notifyObservers(Object data) {
		int size = 0;
		PreferencesResetObserver[] arrays = null;
		synchronized (this) {
			if (hasChanged()) {
				clearChanged();
				size = observers.size();
				arrays = new PreferencesResetObserver[size];
				observers.toArray(arrays);
			}
		}
		if (arrays != null) {
			for (PreferencesResetObserver observer : arrays) {
				observer.update(this, data);
			}
		}
	}

	/**
	 * Sets the changed flag for this {@code Observable}. After calling
	 * {@code setChanged()}, {@code hasChanged()} will return {@code true}.
	 */
	protected void setChanged() {
		changed = true;
	}
}