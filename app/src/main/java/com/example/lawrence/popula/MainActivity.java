package com.example.lawrence.popula;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static Random rand;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    ImageView startButton;

    Button enterNameInputButton;
    Button frequencyInputButton;
    Button numTextsButton;
    Button textsFromButton;

    EditText nameEditText;
    EditText frequencyEditText;
    EditText numTextsEditText;
    EditText textsFromEditText;

    Timer timer;
    Activity activity;

    String personName = "Bob";
    double timeBetweenMessages = 5000;
    long numTexts = 20;
    int wordComplexity;
    ArrayList<String> textsFrom;
    boolean running;
    int notificationNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Locale.setDefault(Locale.US);
        rand = new Random();
        activity = this;
        timer = new Timer();
        textsFrom = new ArrayList<>();

        mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.notification);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        frequencyInputButton = (Button)findViewById(R.id.frequencyInputButton);
        frequencyEditText = (EditText)findViewById(R.id.frequencyInput);
        frequencyInputButton.setOnClickListener(this);

        numTextsButton = (Button)findViewById(R.id.repeatButton);
        numTextsEditText = (EditText)findViewById(R.id.timesRepeatedInput);
        numTextsButton.setOnClickListener(this);

        startButton = (ImageView)findViewById(R.id.startButton);
        startButton.setImageResource(R.drawable.play);
        startButton.setOnClickListener(this);

        enterNameInputButton = (Button)findViewById(R.id.nameInputButton);
        nameEditText = (EditText)findViewById(R.id.nameInput);
        enterNameInputButton.setOnClickListener(this);

        textsFromButton = (Button)findViewById(R.id.girlfriendsButton);
        textsFromEditText = (EditText)findViewById(R.id.girlfriendsInput);
        textsFromButton.setOnClickListener(this);
    }

            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                switch(v.getId()) {
                    case R.id.startButton:
                        startButton.setOnClickListener(this);
                        if (!running) {
                            running = true;
                            startButton.setImageResource(R.drawable.pause);
                            sendNotifications();
                            Object LOCK = new Object();
                            synchronized (LOCK) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                }
                            }
                            startButton.setImageResource(R.drawable.play);
                            running = false;
                        } else {
                            startButton.setImageResource(R.drawable.play);
                            running = false;
                        }
                        break;

                    case R.id.nameInputButton:
                        if (!nameEditText.getText().toString().equals("")) {
                            personName = nameEditText.getText().toString();
                        }
                        enterNameInputButton.setText("");
                        nameEditText.setHint(personName);
                        nameEditText.clearFocus();
                        break;

                    case R.id.frequencyInputButton:
                        if (!(frequencyEditText.getText().toString().equals(""))) {
                            try { timeBetweenMessages = Double.parseDouble(frequencyEditText.getText().toString()) * 1000; }
                            catch (NumberFormatException e) { //resolved with setText
                            }
                        }
                            frequencyEditText.setText("");
                            frequencyEditText.setHint(Double.toString(timeBetweenMessages / 1000));
                            frequencyEditText.clearFocus();
                            break;

                            case R.id.repeatButton:
                                if (!numTextsEditText.getText().toString().equals("")) {
                                    try { numTexts = Long.parseLong(numTextsEditText.getText().toString()); }
                                    catch (NumberFormatException e) { //resolved with setText
                                    }
                                }
                                numTextsEditText.setText("");
                                numTextsEditText.setHint(Long.toString(numTexts));
                                numTextsEditText.clearFocus();
                                break;

                            case R.id.girlfriendsButton:
                                if (!textsFromEditText.getText().toString().equals("")) {
                                    textsFrom.add(textsFromEditText.getText().toString());
                                }
                }
            }

            public void sendNotifications() {
                new Thread(new Runnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        synchronized (this) {
                            for (i = notificationNum; i < numTexts; i++) {
                                try {
                                    Thread.sleep((long) (timeBetweenMessages * rand.nextDouble()));
                                } catch (InterruptedException e) { //doesn't matter
                                }
                                if (!running) {
                                    return;
                                }
                                mBuilder.setContentTitle(randomName("person_names", activity));
                                mBuilder.setContentText(randomSentence());
                                mNotificationManager.notify(notificationNum, mBuilder.build());
                                notificationNum++;
                            }
                            notify();
                        }
                    }
                }).start();
            }

    public String randomSentence() {
            int sentenceStructure = rand.nextInt(10);
            switch(sentenceStructure) {
                case 1: return"You are sooooo " + randomString("adjectives", this);
                case 2: return randomQuestion();
                case 3: return randomGreeting() + " " + personName;
                case 4: return randomString("verbs", this) + " " + randomString("adverbs", this) + " " + personName;
                case 5: return "Want to " + randomString("verbs", this) + " at my " + randomString("nouns", this) + " " + personName;
                case 6: return randomQuestion() + " do you " + randomString("verbs", this) + " " + personName;
                case 7: return randomString("verbs", this) + " " + randomPreposition() + " " + randomString("nouns", this);
                case 8: return "talk to me pls";
                case 9: return randomConjunction() + " " + randomArticle() + " " + randomString("adjectives", this) + " " + randomString("nouns", this) + " " + randomString("verbs", this) + " " + randomString("adverbs", this);
            }
            return "";
    }

    public static String randomString(String path, Context context) {
        String word = "";
        int beginningOfWord = 0;
        int randomNum2 = rand.nextInt(context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).length() - 5);
        for(int p = 1; p < 30; p++) {
            if(context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).charAt(randomNum2 - p) == ' ') {
                beginningOfWord = randomNum2 - p + 1;
                break;
            }
        }
        for(int p = 1; p < 30; p++) {
            if(context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).charAt(beginningOfWord + p) == ' ') {
                word = context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).toString().substring(beginningOfWord, beginningOfWord + p);
                for(int b = 0; b < word.length(); b++) {
                    if(word.charAt(b) == '_') {
                        word = word.substring(0, b) + " " + word.substring(b + 1, word.length());
                    }
                }
                return word;
            }
        }
        return word;
    }

    public static String randomName(String path, Context context) {
        int randomNum = rand.nextInt(1000);
        for(int p = 0; p < context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).length() - String.valueOf(randomNum).length(); p++) {
            if ((context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).toString().substring(p, p + String.valueOf(randomNum).length())).equals(Integer.toString(randomNum))) {
                for (int b = 1; b < 100; b++) {
                    if (p + b + String.valueOf(randomNum).length() < context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).length() && context.getText(R.string.person_names).charAt(p + b + String.valueOf(randomNum).length()) == ' ') {
                        return context.getText(context.getResources().getIdentifier(path, "string", context.getPackageName())).toString().substring(p + String.valueOf(randomNum).length() + 1, p + String.valueOf(randomNum).length() + b + 1);
                    }
                }

            }
        }
        return "";
    }

    public static String randomArticle() {
        int articleNum = rand.nextInt(20);
        switch(articleNum) {
            case 0: return "the";
            case 1: return "some";
            case 2: return "a";
            case 3: return "no";
            case 4: return "any";
            case 5: return "such";
            case 6: return "few";
            case 7: return "every";
            case 8: return "more";
        }
        return "";
    }

    public static String randomConjunction() {
        Random rand = new Random();
        int conjunctionNum = rand.nextInt(20);
        switch(conjunctionNum) {
            case 0: return "for";
            case 1: return "and";
            case 2: return "nor";
            case 3: return "but";
            case 4: return "or";
            case 5: return "yet";
            case 6: return "so";
        }
        return "";
    }

    public static String randomPreposition() {
        int prepositionNum = rand.nextInt(46);
        switch(prepositionNum) {
            case 0:  return "with";
            case 1: return "         at";
            case 2:return "from";
            case 3: return "     into";
            case 4:return "during";
            case 5: return "      including                 ";
            case 6: return"until";
            case 7:return "       against";
            case 8:return"among";
            case 9:return    "    throughout";
            case 10:return  "despite";
            case 11: return   "        towards";
            case 12: return  "upon";
            case 13: return   "       concerning";
            case 14:  return "of";
            case 15: return    "       to";
            case 16:  return  "in";
            case 17:  return " for";
            case 18: return  "on";
            case 19:return     "      by";
            case 20: return  "about";
            case 21: return   "      like";
            case 22: return  "through";
            case 23:  return   "      over";
            case 24: return  "before";
            case 25: return   "       between";
            case 26:  return " after";
            case 27:  return   "       since";
            case 28: return   "without";
            case 29:  return "         under";
            case 30: return "   within";
            case 31: return "         along";
            case 32: return    "following";
            case 33: return   "       across";
            case 34:  return "  behind";
            case 35: return "           beyond";
            case 36: return   " plus";
            case 37: return  "         except";
            case 38: return "  but";
            case 39:return "           up";
            case 40:return"     out";
            case 41:  return   "      around";
            case 42: return   "down";
            case 43: return  "        off";
            case 44: return "above";
            case 45: return  "       near";
        }
        return "";
    }

    public static String randomGreeting() {
        int randomGreetingNum = rand.nextInt(13);
            switch(randomGreetingNum) {
                case 0: return "hello";
                case 1: return "good morning";
                case 2: return "what's up?";
                case 3: return "good afternoon";
                case 4: return "good evening";
                case 5: return "how've you been?";
                case 6: return "how's it going?";
                case 7: return "hi";
                case 8: return "how are you?";
                case 9: return "hey";
                case 10:return "see you later";
                case 11:return "bye";
                case 12:return "good night";
            }
            return "";
        }

        public static String randomQuestion() {
            int randomQuestionNum = rand.nextInt(8);
            switch(randomQuestionNum) {
                case 0: return "why";
                case 1: return "how";
                case 2: return "where";
                case 3: return "when";
                case 4: return "how";
                case 5: return "which";
                case 6: return "who";
                case 7: return "what";
            }
            return "";
        }

    }

