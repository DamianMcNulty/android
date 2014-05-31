package org.sagemath.droid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Haven on 28-05-2014.
 */

/*
    {
  "content":{
    "data":{
      "application\/sage-interact":{
        "new_interact_id":"a3207d5c-fbbc-4a84-9937-bf1993722c9a",
        "controls":{
          "n":{
            "update":true,
            "raw":true,
            "control_type":"slider",
            "display_value":true,
            "values":[
              "1",
              "2",
              "3",
              "4",
              "5",
              "6",
              "7",
              "8",
              "9",
              "10"
            ],
            "default":0,
            "range":[
              0,
              9
            ],
            "subtype":"discrete",
            "label":"n",
            "step":1
          }
        },
        "readonly":false,
        "locations":null,
        "layout":[
          [
            [
              "n",
              1
            ]
          ],
          [
            [
              "_output",
              1
            ]
          ]
        ]
      },
      "text\/plain":"Sage Interact"
    },
    "source":"sagecell"
  },
  "msg_id":"ad2af746-7658-40b1-8dde-9177931345b1",
  "parent_header":{
    "msg_id":"cb77485c-dbd4-40f4-9194-8c018eb05a67",
    "session":"668bca78-ad3b-4e77-8d75-0f7439b1f4f4",
    "username":"",
    "msg_type":"execute_request"
  },
  "header":{
    "msg_id":"ad2af746-7658-40b1-8dde-9177931345b1",
    "username":"kernel",
    "date":"2014-05-28T10:35:06.986527",
    "session":"685eb162-eb44-4df3-83e7-a48f4908663b",
    "msg_type":"display_data"
  },
  "metadata":{

  },
  "msg_type":"display_data"
}

 */
public class InteractReply extends BaseReply {

    private InteractContent content;

    public InteractContent getContent() {
        return content;
    }

    public static class InteractContent {

        @Expose
        private InteractData data;
        private String source;

        public InteractData getData() {
            return data;
        }

        public void setData(InteractData data) {
            this.data = data;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class InteractData {
        //---POSSIBLE SAGE REPLIES---
        @SerializedName("application/sage-interact")
        private SageInteract sageInteract;

        @SerializedName("text/plain")
        private String descText;

        public SageInteract getInteract() {
            return sageInteract;
        }

        public void setInteract(SageInteract sageInteract) {
            this.sageInteract = sageInteract;
        }

        public String getDescText() {
            return descText;
        }

        public void setDescText(String descText) {
            this.descText = descText;
        }
    }

    public static class SageInteract {

        private String new_interact_id;
        private InteractControl controls;
        private boolean readonly;
        private String locations;
        private ArrayList<ArrayList<ArrayList<String>>> layout;

        public String getNewInteractID() {
            return new_interact_id;
        }

        public void setNewInteractID(String new_interact_id) {
            this.new_interact_id = new_interact_id;
        }

        public InteractControl getControls() {
            return controls;
        }

        public void setControls(InteractControl controls) {
            this.controls = controls;
        }

        public boolean isReadonly() {
            return readonly;
        }

        public void setReadonly(boolean readonly) {
            this.readonly = readonly;
        }

        public String getLocations() {
            return locations;
        }

        public void setLocations(String locations) {
            this.locations = locations;
        }

        public ArrayList<ArrayList<ArrayList<String>>> getLayout() {
            return layout;
        }

        public void setLayout(ArrayList<ArrayList<ArrayList<String>>> layout) {
            this.layout = layout;
        }
    }

    public static class InteractControl {

        //The variable name that this control is associated with
        //Not part of the JSON that is received or replied
        //So we mark it transient to prevent GSON from deserialising/serialising it
        private transient String varName;

        private boolean update;
        private boolean raw;
        private String control_type;
        private boolean display_value;
        private String[] values;

        @SerializedName("default")
        private int _default;

        private int[] range;
        private String subtype;
        private String label;
        private int step;

        public boolean isUpdate() {
            return update;
        }

        public boolean isRaw() {
            return raw;
        }

        public String getControlType() {
            return control_type;
        }

        public boolean isDisplayValue() {
            return display_value;
        }

        public String[] getValues() {
            return values;
        }

        public int getDefault() {
            return _default;
        }

        public int[] getRange() {
            return range;
        }

        public String getSubtype() {
            return subtype;
        }

        public String getLabel() {
            return label;
        }

        public int getStep() {
            return step;
        }

        public void setUpdate(boolean update) {
            this.update = update;
        }

        public void setRaw(boolean raw) {
            this.raw = raw;
        }

        public void setControlType(String control_type) {
            this.control_type = control_type;
        }

        public void setDisplayValue(boolean display_value) {
            this.display_value = display_value;
        }

        public void setValues(String[] values) {
            this.values = values;
        }

        public void setDefault(int _default) {
            this._default = _default;
        }

        public void setRange(int[] range) {
            this.range = range;
        }

        public void setSubtype(String subtype) {
            this.subtype = subtype;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public String getVarName() {
            return varName;
        }

        public void setVarName(String varName) {
            this.varName = varName;
        }
    }

}
