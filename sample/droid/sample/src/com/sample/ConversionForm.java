package com.sample;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.calatrava.CalatravaPage;
import com.calatrava.bridge.RegisteredActivity;

@CalatravaPage(name = "conversionForm")
public class ConversionForm extends RegisteredActivity implements
		OnItemSelectedListener {

	TextView convertedTextView;
	EditText unconvertedEditText;
	Spinner inSpinner, outSpinner, spin;
	ArrayList<String> inCurrencyStrings, outCurrencyStrings;
	ArrayAdapter<String> inCurAdapter, outCurAdapter, adapt;
	String inCurrencyCode = "USD";
	String outCurrencyCode = "USD";
	JSONArray inJsonArray, outJsonArray;
	JSONObject jsonobj;
	String key;

	@Override
	protected String getPageName() {
		return "conversionForm";
	}

	@Override
	protected void onCreate(Bundle availableData) {
		// TODO Auto-generated method stub
		super.onCreate(availableData);
		setContentView(R.layout.main);
		convertedTextView = (TextView) findViewById(R.id.convertedTextView);
		unconvertedEditText = (EditText) findViewById(R.id.unconvertedEditText);
		inSpinner = (Spinner) findViewById(R.id.inSpinner);
		outSpinner = (Spinner) findViewById(R.id.outSpinner);
		inCurrencyStrings = new ArrayList<String>();
		outCurrencyStrings = new ArrayList<String>();

		inSpinner.setOnItemSelectedListener(this);
		outSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public String getFieldValue(String field) {
		// TODO Auto-generated method stub
		if (field.equalsIgnoreCase("in_amount")) {
			return unconvertedEditText.getText().toString();
		} else if (field.equalsIgnoreCase("in_currency")) {
			return inCurrencyCode;
		} else if (field.equalsIgnoreCase("out_currency")) {
			return outCurrencyCode;
		}
		return null;
	}

	@Override
	public void render(String json) {
		try {
			jsonobj = new JSONObject(json);
			for (Iterator<String> iter = jsonobj.keys(); iter.hasNext();) {

				key = String.valueOf(iter.next());
				if (key.equalsIgnoreCase("inCurrencies")) {
					renderSpinner(inSpinner, jsonobj.getJSONArray(key),
							inCurrencyStrings, inCurAdapter);
				} else if (key.equalsIgnoreCase("outCurrencies")) {
					renderSpinner(inSpinner, jsonobj.getJSONArray(key),
							outCurrencyStrings, outCurAdapter);
				} else if (key.equalsIgnoreCase("in_amount")) {
					this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								unconvertedEditText.setText(jsonobj.getString(key));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} else if (key.equalsIgnoreCase("out_amount")) {
					this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								convertedTextView.setText(jsonobj.getString(key));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void convert(View v) {
		this.triggerEvent("convert", new String[] {});
	}

	public void renderSpinner(Spinner spinner, JSONArray data,
			ArrayList<String> currencyStrings, ArrayAdapter<String> adapter)
			throws JSONException {

		currencyStrings.clear();

		for (int i = 0; i < data.length(); i++) {
			currencyStrings.add(data.getJSONObject(i).getString("code"));
		}

		if (spinner == inSpinner) {
			inJsonArray = data;
		} else if (spinner == outSpinner) {
			outJsonArray = data;
		}

		if (adapter == null) {
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, currencyStrings);
		} else {
			adapter.clear();
			adapter.addAll(currencyStrings);
		}

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin = spinner;
		adapt = adapter;

		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				spin.setAdapter(adapt);
			}
		});

		Log.e("error", adapter.toString());

		Log.e("error", String.valueOf(adapter.getCount()));
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		String event = parent == inSpinner ? "selectedInCurrency"
				: "selectedOutCurrency";
		JSONArray jsonArray = parent == inSpinner ? inJsonArray : outJsonArray;
		String currencyCode = "USD";

		Log.e("error", jsonArray.toString());
		Log.e("error", event);

		if (parent == inSpinner) {
			inCurrencyCode = (String) parent.getItemAtPosition(pos);
			// try {
			// if(jsonArray.getJSONObject(pos).getBoolean("enabled") == false) {
			// inCurrencyCode = (String) parent.getItemAtPosition((pos + 1) %
			// jsonArray.length());
			// parent.setSelection((pos + 1) % jsonArray.length());
			// }
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			currencyCode = inCurrencyCode;
		} else if (parent == outSpinner) {
			outCurrencyCode = (String) parent.getItemAtPosition(pos);
			// try {
			// if(jsonArray.getJSONObject(pos).getBoolean("enabled") == false) {
			// outCurrencyCode = (String) parent.getItemAtPosition((pos + 1) %
			// jsonArray.length());
			// parent.setSelection((pos + 1) % jsonArray.length());
			// }
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			currencyCode = outCurrencyCode;
		}

		this.triggerEvent(event, new String[] { currencyCode });
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
