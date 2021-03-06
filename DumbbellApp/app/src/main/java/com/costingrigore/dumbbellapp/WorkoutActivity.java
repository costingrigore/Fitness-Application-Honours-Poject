package com.costingrigore.dumbbellapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import static android.view.View.GONE;

/**
 * This class handles the workout routine creation algorithm functionality
 * The class constructs a workout routine, using the user's level of experience, goal for using the application, body area target and level of difficulty selected by the user
 * The activity_workout.xml file that corresponds with this script, contains the workout set up page, the workout routine pages and the feedback form pages
 */
public class WorkoutActivity extends AppCompatActivity {
    /**
     * Initialising the buttons used to start the workout, finish the workout and to get new exercises
     */
    Button startWorkoutButton;
    Button nextExercise;
    Button finishWorkout;
    /**
     * Field used to set the column of the class' layout
     */
    private int mColumnCount = 1;
    /**
     * Default values for the level of experience and the goal for using the application of the workout routine
     */
    private String levelOfExperience = "Beginner";
    private String goal = "Be fit";
    /**
     * Strings storing the workout's body area target and the workout's difficulty level
     */
    private String workoutBodyArea;
    private String workoutDifficulty;
    /**
     * Initializing Firebase
     */
    FirebaseDatabase database;
    /**
     * IDs used to identify the level of experience of the user for the workout routine
     */
    private int beginnerID = 0;
    private int intermediateID = 1;
    private int advancedID = 2;
    private int professionalID = 3;
    /**
     * IDs used to identify the goal for using the application of the user for the workout routine
     */
    private int beFitID = 0;
    private int loseWeightID = 1;
    private int gainStrengthID = 2;
    /**
     * Initializing the array lists that will store the exercises required for the workout routine
     */
    public ArrayList<Exercise> cardioExercises = new ArrayList<>();
    public ArrayList<Exercise> weightTrainingExercises = new ArrayList<>();
    public ArrayList<Exercise> coreExercises = new ArrayList<>();
    public ArrayList<Exercise> stretchingExercises = new ArrayList<>();
    /**
     * Initializing the recycler views and the adapters needed to display the exercises in the application
     */
    RecyclerView cardioExercisesRecyclerView = null;
    WorkoutActivityRecyclerViewAdapter cardioExercisesAdapter;
    RecyclerView weightTrainingExercisesRecyclerView = null;
    WorkoutActivityRecyclerViewAdapter weightTrainingExercisesAdapter;
    RecyclerView coreExercisesRecyclerView = null;
    WorkoutActivityRecyclerViewAdapter coreExercisesAdapter;
    RecyclerView stretchingExercisesRecyclerView = null;
    WorkoutActivityRecyclerViewAdapter stretchingExercisesAdapter;
    /**
     * Initializing the integers used to store the number of exercises used in every component of the workout routine
     */
    int cardioAmountOfExercises;
    int weightTrainingAmountOfExercises;
    int coreAmountOfExercises;
    int stretchingAmountOfExercises;
    /**
     * Used to store the current number of exercises
     */
    int currentAmountOfExercises;
    /**
     * 2D integer array used to set the cardio time that each workout routine needs based on the level of experience of the user and the goal for using the application
     * It follows the following structure:
     * -                Beginner    Intermediate    Advanced    Professional
     * Be Fit           15          20              25          30
     * Lose Weight      20          25              30          30
     * Gain Strength    15          15              20          25
     */
    private int[][] cardioTimeValues =
            {
                    {15, 20, 25, 30},
                    {20, 25, 30, 30},
                    {15, 15, 20, 25}
            };
    /**
     * 2D integer array used to set the weight training time that each workout routine needs based on the level of experience of the user and the goal for using the application
     * It follows the following structure:
     * -                Beginner    Intermediate    Advanced    Professional
     * Be Fit           10          15              15          20
     * Lose Weight      10          10              15          20
     * Gain Strength    20          20              25          30
     */
    private int[][] weightTrainingTimeValues =
            {
                    {10, 15, 15, 20},
                    {10, 10, 15, 20},
                    {20, 20, 25, 30}
            };
    /**
     * 2D integer array used to set the core time that each workout routine needs based on the level of experience of the user and the goal for using the application
     * It follows the following structure:
     * -                Beginner    Intermediate    Advanced    Professional
     * Be Fit           10          10              15          15
     * Lose Weight      05          05              10          10
     * Gain Strength    10          10              15          20
     */
    private int[][] coreTimeValues =
            {
                    {10, 10, 15, 15},
                    {5, 5, 10, 10},
                    {10, 10, 15, 20}
            };
    /**
     * Default time assigned for stretching exercises
     */
    private int stretchingTimeValue = 8;
    /**
     * Initializing the required fields for the cardio section of the workout application
     */
    CheckBox cardioSetsAndRepsCB;
    LinearLayout cardioSetsAndRepsLayout;
    NumberPicker cardioSets;
    NumberPicker cardioReps;
    CheckBox cardioTimeCB;
    LinearLayout cardioTimeLayout;
    NumberPicker cardioTime;
    /**
     * By default, if the user does not pick between using sets and repetitions or using minutes to show the exercises, cardio exercises will use time, with one minute as the default length of each exercise
     */
    boolean cardioUsesTime = true;
    /**
     * Setting the maximum amount of sets, repetitions and time in minutes that the user can pick for cardio exercises
     */
    int maxCardioSets = 10;
    int maxCardioReps = 60;
    int maxCardioTime = 10;
    /**
     * Initializing the required fields for the weight training section of the workout application
     */
    CheckBox wtSetsAndRepsCB;
    LinearLayout wtSetsAndRepsLayout;
    NumberPicker wtSets;
    NumberPicker wtReps;
    CheckBox wtTimeCB;
    LinearLayout wtTimeLayout;
    NumberPicker wtTime;
    /**
     * By default, if the user does not pick between using sets and repetitions or using minutes to show the exercises, weight training exercises will use time, with one minute as the default length of each exercise
     */
    boolean wtUsesTime = true;
    /**
     * Setting the maximum amount of sets, repetitions and time in minutes that the user can pick for weight training exercises
     */
    int maxWtSets = 10;
    int maxWtReps = 60;
    int maxWtTime = 10;
    /**
     * By default, if the user does not pick between using sets and repetitions or using minutes to show the exercises, core exercises will use time, with one minute as the default length of each exercise
     */
    CheckBox coreSetsAndRepsCB;
    LinearLayout coreSetsAndRepsLayout;
    NumberPicker coreSets;
    NumberPicker coreReps;
    CheckBox coreTimeCB;
    LinearLayout coreTimeLayout;
    NumberPicker coreTime;
    /**
     * By default, if the user does not pick between using sets and repetitions or using minutes to show the exercises, core exercises will use time, with one minute as the default length of each exercise
     */
    boolean coreUsesTime = true;
    /**
     * Setting the maximum amount of sets, repetitions and time in minutes that the user can pick for cardio exercises
     */
    //
    int maxCoreSets = 10;
    int maxCoreReps = 60;
    int maxCoreTime = 10;
    /**
     * Initializing the fields that will store the number of sets, repetitions and minutes for each component of the workout routine, by default they are all set to 1
     */
    int cardioSetsNumber = 1;
    int cardioRepsNumber = 1;
    int cardioTimeNumber = 1;
    int wtSetsNumber = 1;
    int wtRepsNumber = 1;
    int wtTimeNumber = 1;
    int coreSetsNumber = 1;
    int coreRepsNumber = 1;
    int coreTimeNumber = 1;
    /**
     * Integers used to store the number of easy, medium and difficult exercises
     */
    int amountEasyExercises = 0;
    int amountMediumExercises = 0;
    int amountDifficultExercises = 0;
    /**
     * Initializing all the layouts, text views and the image view used in the workout activity
     */
    LinearLayout initialLayout;
    LinearLayout workoutLayout;
    TextView componentTitle;
    TextView exerciseCount;
    ImageView exerciseImage;
    TextView exerciseName;
    LinearLayout exerciseSetsAndRepetitionsLayout;
    TextView exerciseSets;
    TextView exerciseReps;
    LinearLayout exerciseTimeLayout;
    TextView exerciseTime;
    /**
     * Initializing the fields used for the second part of the workout activity, where the user has started the workout routine and each individual excercise displays
     */
    int currentExercise = 0;
    String currentComponent = "Component 2: Cardio";
    /**
     * String used to keep track of the current exercise being displayed in the application
     */
    String exerciseCountString = "";
    /**
     * Setting up fields for the timer count down
     * The timer count down is used when the user selects to use time as their measure for doing their exercises
     * I used the following sources to develop the timer count down:
     * https://codinginflow.com/tutorials/android/countdowntimer/part-1-countdown-timer
     * https://www.youtube.com/watch?v=MDuGwI6P-X8
     */
    private static final long START_TIME_IN_MILLIS = 60000;
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    /**
     * Setting the EditText objects and TextView objects used for the feedback form algorithm
     */
    private EditText cardioEasyExercisesET, cardioMediumExercisesET, cardioDifficultExercisesET, cardioSetsET, cardioRepetitionsET, cardioMinutesET;
    private EditText weightTrainingEasyExercisesET, weightTrainingMediumExercisesET, weightTrainingDifficultExercisesET, weightTrainingSetsET, weightTrainingRepetitionsET, weightTrainingMinutesET;
    private EditText coreEasyExercisesET, coreMediumExercisesET, coreDifficultExercisesET, coreSetsET, coreRepetitionsET, coreMinutesET;
    private TextView stretchingEasyExercisesTV, stretchingMediumExercisesTV, stretchingDifficultExercisesTV;
    private EditText stretchingMinutesET;
    private TextView feedbackLevelOfExperienceTV, feedbackWorkoutDifficultyTV, feedbackWorkoutBodyAreaTargetTV;
    private LinearLayout workoutFeedbackLayout;

    /**
     * Called when the activity gets created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        /**
         * Getting the fields needed as parameters for the workout routine creation algorithm from the intent that created the current activity
         */
        Intent intent = getIntent();
        workoutBodyArea = intent.getStringExtra("Body Area");
        workoutDifficulty = intent.getStringExtra("Difficulty");
        levelOfExperience = intent.getStringExtra("Level of experience");
        goal = intent.getStringExtra("Goal");
        /**
         * Initializing the timer buttons and text view
         */
        mTextViewCountDown = findViewById(R.id.exercise_time);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        /**
         * Setting up the onClick listener of the start button of the timer
         */
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        /**
         * Setting up the onClick listener of the reset button of the timer
         */
        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
        /**
         * Update the count down text
         */
        updateCountDownText();
        /**
         *
         */
        //Initializing Firebase
        database = FirebaseDatabase.getInstance();
        /**
         * Processing the user's personal data as fields of this activity
         */
        ProcessUserPersonalData();
        /**
         * Workout Routine Creation Algorithm's fields set up
         * Setting the recycler views, adapters and linear layouts of the containers where the exercises will be displayed
         *
         * Setting cardio exercises recycler view, linear layout for the recycler view and the adapter for the recycler view
         **/
        cardioExercisesRecyclerView = (RecyclerView) this.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            cardioExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            cardioExercisesRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        LinearLayoutManager horizontalLayoutManager1
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cardioExercisesRecyclerView.setLayoutManager(horizontalLayoutManager1);
        cardioExercisesAdapter = new WorkoutActivityRecyclerViewAdapter(cardioExercises);
        cardioExercisesRecyclerView.setAdapter(cardioExercisesAdapter);
        /**
         * Setting weight training recycler view, linear layout for the recycler view and the adapter for the recycler view
         */
        weightTrainingExercisesRecyclerView = (RecyclerView) this.findViewById(R.id.list2);
        if (mColumnCount <= 1) {
            weightTrainingExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            weightTrainingExercisesRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        LinearLayoutManager horizontalLayoutManager2
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weightTrainingExercisesRecyclerView.setLayoutManager(horizontalLayoutManager2);
        weightTrainingExercisesAdapter = new WorkoutActivityRecyclerViewAdapter(weightTrainingExercises);
        weightTrainingExercisesRecyclerView.setAdapter(weightTrainingExercisesAdapter);
        /**
         * Setting core exercises recycler view, linear layout for the recycler view and the adapter for the recycler view
         */
        coreExercisesRecyclerView = (RecyclerView) this.findViewById(R.id.list3);
        if (mColumnCount <= 1) {
            coreExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            coreExercisesRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        LinearLayoutManager horizontalLayoutManager3
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        coreExercisesRecyclerView.setLayoutManager(horizontalLayoutManager3);
        coreExercisesAdapter = new WorkoutActivityRecyclerViewAdapter(coreExercises);
        coreExercisesRecyclerView.setAdapter(coreExercisesAdapter);
        /**
         * Setting stretching exercises recycler view, linear layout for the recycler view and the adapter for the recycler view
         */
        stretchingExercisesRecyclerView = (RecyclerView) this.findViewById(R.id.list4);
        if (mColumnCount <= 1) {
            stretchingExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            stretchingExercisesRecyclerView.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        LinearLayoutManager horizontalLayoutManager4
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        stretchingExercisesRecyclerView.setLayoutManager(horizontalLayoutManager4);
        stretchingExercisesAdapter = new WorkoutActivityRecyclerViewAdapter(stretchingExercises);
        stretchingExercisesRecyclerView.setAdapter(stretchingExercisesAdapter);
        /**
         * 2D arrays used to set the number of easy, medium and difficult exercises
         * They use the following structure:
         * -                                    No easy exercises   No medium exercises No difficult exercises
         * amountEasyExercisesArray             2                   1                   1
         * amountMediumExercisesArray           1                   2                   1
         * amountDifficultExercisesArray        1                   1                   2
         * It gets assigned based on the level of difficulty the user has selected for the workout routine
         */
        int[] amountEasyExercisesArray = {2, 1, 1};
        int[] amountMediumExercisesArray = {1, 2, 1};
        int[] amountDifficultExercisesArray = {1, 1, 2};
        /**
         * Depending on the level of difficulty picked by the user the number of easy, medium and difficult exercises gets changed accordingly
         */
        if (workoutDifficulty.equals("easy")) {
            amountEasyExercises = amountEasyExercisesArray[0];
            amountMediumExercises = amountMediumExercisesArray[0];
            amountDifficultExercises = amountDifficultExercisesArray[0];
        }
        if (workoutDifficulty.equals("medium")) {
            amountEasyExercises = amountEasyExercisesArray[1];
            amountMediumExercises = amountMediumExercisesArray[1];
            amountDifficultExercises = amountDifficultExercisesArray[1];
        }
        if (workoutDifficulty.equals("difficult")) {
            amountEasyExercises = amountEasyExercisesArray[2];
            amountMediumExercises = amountMediumExercisesArray[2];
            amountDifficultExercises = amountDifficultExercisesArray[2];
        }
        /**
         * Getting all the necessary exercises for the workout routine, by calling GetPersonalisedExercises for the cardio, weight training, core and stretching components
         *
         * For the cardio component, the exercise type is set to be cardio and the body part is set to be total body, as the source of exercises only has total body cardio exercises
         * For the weight training component, the exercise type is set to be strength and the body part is set to be the body area target selected by the user
         * For the core component, the exercise type is set to be strength and the body part is set to be core, as core exercises are considered to be a kind of strength exercises in the source of the exercises
         * For the stretching component, the exercise type is set to be flexibility and the body part is set to be total body, as the source of exercises only has total body stretching exercises
         */
        GetPersonalisedExercises("cardio", "total_body", amountEasyExercises, amountMediumExercises, amountDifficultExercises);
        GetPersonalisedExercises("strength", workoutBodyArea, amountEasyExercises, amountMediumExercises, amountDifficultExercises);
        GetPersonalisedExercises("strength", "core", amountEasyExercises, amountMediumExercises, amountDifficultExercises);
        GetPersonalisedExercises("flexibility", "total_body", amountEasyExercises, amountMediumExercises, amountDifficultExercises);
        /**
         * Setting up checkboxes and number picker fields to retrieve repetitions, sets and time for the workout routine set by the user
         * The minimum values of the components' number of sets, repetitions, and minutes are set to one
         */
        cardioSetsAndRepsCB = (CheckBox) this.findViewById(R.id.cardioSetsandRepsCB);
        cardioSetsAndRepsLayout = (LinearLayout) this.findViewById(R.id.cardioSetsAndRepsLayout);
        cardioSets = (NumberPicker) this.findViewById(R.id.cardioSets);
        cardioSets.setMinValue(1);
        cardioSets.setMaxValue(maxCardioSets);
        cardioReps = (NumberPicker) this.findViewById(R.id.cardioReps);
        cardioReps.setMinValue(1);
        cardioReps.setMaxValue(maxCardioReps);
        cardioTimeCB = (CheckBox) this.findViewById(R.id.cardioTimeCB);
        cardioTimeLayout = (LinearLayout) this.findViewById(R.id.cardioTimeLayout);
        cardioTime = (NumberPicker) this.findViewById(R.id.cardioTime);
        cardioTime.setMinValue(1);
        cardioTime.setMaxValue(maxCardioTime);
        wtSetsAndRepsCB = (CheckBox) this.findViewById(R.id.wtSetsandRepsCB);
        wtSetsAndRepsLayout = (LinearLayout) this.findViewById(R.id.wtSetsAndRepsLayout);
        wtSets = (NumberPicker) this.findViewById(R.id.wtSets);
        wtSets.setMinValue(1);
        wtSets.setMaxValue(maxWtSets);
        wtReps = (NumberPicker) this.findViewById(R.id.wtReps);
        wtReps.setMinValue(1);
        wtReps.setMaxValue(maxWtReps);
        wtTimeCB = (CheckBox) this.findViewById(R.id.wtTimeCB);
        wtTimeLayout = (LinearLayout) this.findViewById(R.id.wtTimeLayout);
        wtTime = (NumberPicker) this.findViewById(R.id.wtTime);
        wtTime.setMinValue(1);
        wtTime.setMaxValue(maxWtTime);
        coreSetsAndRepsCB = (CheckBox) this.findViewById(R.id.coreSetsandRepsCB);
        coreSetsAndRepsLayout = (LinearLayout) this.findViewById(R.id.coreSetsAndRepsLayout);
        coreSets = (NumberPicker) this.findViewById(R.id.coreSets);
        coreSets.setMinValue(1);
        coreSets.setMaxValue(maxCoreSets);
        coreReps = (NumberPicker) this.findViewById(R.id.coreReps);
        coreReps.setMinValue(1);
        coreReps.setMaxValue(maxCoreReps);
        coreTimeCB = (CheckBox) this.findViewById(R.id.coreTimeCB);
        coreTimeLayout = (LinearLayout) this.findViewById(R.id.coreTimeLayout);
        coreTime = (NumberPicker) this.findViewById(R.id.coreTime);
        coreTime.setMinValue(1);
        coreTime.setMaxValue(maxCoreTime);
        /**
         * Setting up the onClick listeners for all the check boxes for the workout activity
         */
        cardioSetsAndRepsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the cardio sets and repetitions check box is checked, uncheck the time check box and display the sets and repetitions selection layout.
                if (cardioSetsAndRepsCB.isChecked()) {
                    cardioSetsAndRepsCB.setChecked(true);
                    cardioTimeCB.setChecked(false);
                    cardioUsesTime = false;
                    cardioSetsAndRepsLayout.setVisibility(View.VISIBLE);
                    cardioTimeLayout.setVisibility(GONE);
                }
            }
        });
        cardioTimeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the cardio time check box is checked, uncheck the sets and repetitions check box and display the time selection layout.
                if (cardioTimeCB.isChecked()) {
                    cardioTimeCB.setChecked(true);
                    cardioSetsAndRepsCB.setChecked(false);
                    cardioUsesTime = true;
                    cardioTimeLayout.setVisibility(View.VISIBLE);
                    cardioSetsAndRepsLayout.setVisibility(GONE);
                }
            }
        });

        wtSetsAndRepsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the weight training sets and repetitions check box is checked, uncheck the time check box and display the sets and repetitions selection layout.
                if (wtSetsAndRepsCB.isChecked()) {
                    wtSetsAndRepsCB.setChecked(true);
                    wtTimeCB.setChecked(false);
                    wtUsesTime = false;
                    wtSetsAndRepsLayout.setVisibility(View.VISIBLE);
                    wtTimeLayout.setVisibility(GONE);
                }
            }
        });
        wtTimeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the weight training time check box is checked, uncheck the sets and repetitions check box and display the time selection layout.
                if (wtTimeCB.isChecked()) {
                    wtTimeCB.setChecked(true);
                    wtSetsAndRepsCB.setChecked(false);
                    wtUsesTime = true;
                    wtTimeLayout.setVisibility(View.VISIBLE);
                    wtSetsAndRepsLayout.setVisibility(GONE);
                }
            }
        });

        coreSetsAndRepsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the core sets and repetitions check box is checked, uncheck the time check box and display the sets and repetitions selection layout.
                if (coreSetsAndRepsCB.isChecked()) {
                    coreSetsAndRepsCB.setChecked(true);
                    coreTimeCB.setChecked(false);
                    coreUsesTime = false;
                    coreSetsAndRepsLayout.setVisibility(View.VISIBLE);
                    coreTimeLayout.setVisibility(GONE);
                }
            }
        });
        coreTimeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the core time check box is checked, uncheck the sets and repetitions check box and display the time selection layout.
                if (coreTimeCB.isChecked()) {
                    coreTimeCB.setChecked(true);
                    coreSetsAndRepsCB.setChecked(false);
                    coreUsesTime = true;
                    coreTimeLayout.setVisibility(View.VISIBLE);
                    coreSetsAndRepsLayout.setVisibility(GONE);
                }
            }
        });
        /**
         * Setting up the onValueChangedListeners for the number pickers used for sets, repetitions, time (in minutes) for the cardio, weight training and core components
         */
        cardioSets.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cardioSetsNumber = cardioSets.getValue();
            }
        });
        cardioReps.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cardioRepsNumber = cardioReps.getValue();
            }
        });
        cardioTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                cardioTimeNumber = cardioTime.getValue();
            }
        });

        wtSets.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                wtSetsNumber = wtSets.getValue();
            }
        });
        wtReps.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                wtRepsNumber = wtReps.getValue();
            }
        });
        wtTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                wtTimeNumber = wtTime.getValue();
            }
        });

        coreSets.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                coreSetsNumber = coreSets.getValue();
            }
        });
        coreReps.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                coreRepsNumber = coreReps.getValue();
            }
        });
        coreTime.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                coreTimeNumber = coreTime.getValue();
            }
        });
        /**
         * Setting up all the layouts used in the workout activity
         */
        initialLayout = (LinearLayout) this.findViewById(R.id.initial_layout);
        workoutLayout = (LinearLayout) this.findViewById(R.id.workout_layout);
        componentTitle = (TextView) this.findViewById(R.id.component_title);
        exerciseCount = (TextView) this.findViewById(R.id.exercise_count);
        exerciseImage = (ImageView) this.findViewById(R.id.exerciseID);
        exerciseName = (TextView) this.findViewById(R.id.exercise_name);
        exerciseSetsAndRepetitionsLayout = (LinearLayout) this.findViewById(R.id.exercise_sets_and_reps_layout);
        exerciseSets = (TextView) this.findViewById(R.id.exercise_sets);
        exerciseReps = (TextView) this.findViewById(R.id.exercise_reps);
        exerciseTimeLayout = (LinearLayout) this.findViewById(R.id.exercise_time_layout);
        exerciseTime = (TextView) this.findViewById(R.id.exercise_time);
        /**
         * Setting up the start workout button and the next exercise button in the workout activity
         */
        startWorkoutButton = (Button) this.findViewById(R.id.startWorkoutButton);
        nextExercise = (Button) this.findViewById(R.id.nextButton);
        finishWorkout = (Button) this.findViewById(R.id.finishWorkoutButton);
        /**
         * Setting up onClick listener for start workout button
         */
        startWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide the initial layout of the workout activity and show the workout layout (where each individual exercise is displayed)
                initialLayout.setVisibility(GONE);
                workoutLayout.setVisibility(View.VISIBLE);
                componentTitle.setText(currentComponent);
                //Show the first exercise from the current component (cardio component)
                ShowExercise(cardioExercises.get(currentExercise));
                //If the user picked to use time in minutes for the cardio exercises
                if (cardioUsesTime) {
                    exerciseTimeLayout.setVisibility(View.VISIBLE);
                    exerciseSetsAndRepetitionsLayout.setVisibility(GONE);
                    //Adjusting the way the minutes are displayed in the text view based on the amount of minutes picked by the user
                    if (cardioTimeNumber < 10) {
                        exerciseTime.setText("0" + String.valueOf(cardioTimeNumber) + ":00");
                    }
                    if (cardioTimeNumber >= 10) {
                        exerciseTime.setText(String.valueOf(cardioTimeNumber) + ":00");
                    }
                    //Setting the time left in the timer
                    mTimeLeftInMillis = START_TIME_IN_MILLIS * cardioTimeNumber;
                }
                //If the user picked to use sets and repetitions for the cardio exercises (cardioUsesTime == false)
                else {
                    exerciseTimeLayout.setVisibility(GONE);
                    exerciseSetsAndRepetitionsLayout.setVisibility(View.VISIBLE);
                    exerciseSets.setText(String.valueOf(cardioSetsNumber));
                    exerciseReps.setText(String.valueOf(cardioRepsNumber));
                }
                //Displaying the index of the cardio exercises, so the user can keep track of the current exercise
                exerciseCountString = "Exercise: " + String.valueOf(currentExercise + 1) + "/" + String.valueOf(cardioExercises.size());
                exerciseCount.setText(exerciseCountString);
                currentExercise++;
            }
        });
        finishWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveWorkoutInformationToDatabase();
                finish();
            }
        });

        /**
         * Setting up onClick listener for next exercise button
         */
        nextExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the timer is running from the previous exercise, reset it
                if (mTimerRunning) {
                    pauseTimer();
                    resetTimer();
                    updateCountDownText();
                }
                //If the current component is cardio
                if (currentComponent.equals("Component 2: Cardio")) {
                    componentTitle.setText(currentComponent);
                    //Displaying the index of the cardio exercises, so the user can keep track of the current exercise
                    exerciseCountString = "Exercise: " + String.valueOf(currentExercise + 1) + "/" + String.valueOf(cardioExercises.size());
                    exerciseCount.setText(exerciseCountString);
                    //If the current exercise has not reached the end of the list of cardio exercises
                    if ((currentExercise < (cardioExercises.size()))) {
                        ShowExercise(cardioExercises.get(currentExercise));
                        currentExercise++;
                    }
                    //If the current exercise has reached the end of the list of cardio exercises, change the current component to weight training
                    else if (currentExercise == (cardioExercises.size())) {
                        currentComponent = "Component 3: Weight Training";
                        currentExercise = 0;
                    }
                    //If the user picked to use time in minutes for the cardio exercises
                    if (cardioUsesTime) {
                        exerciseTimeLayout.setVisibility(View.VISIBLE);
                        exerciseSetsAndRepetitionsLayout.setVisibility(GONE);
                        //Adjusting the way the minutes are displayed in the text view based on the amount of minutes picked by the user
                        if (cardioTimeNumber < 10) {
                            exerciseTime.setText("0" + String.valueOf(cardioTimeNumber) + ":00");
                        }
                        if (cardioTimeNumber >= 10) {
                            exerciseTime.setText(String.valueOf(cardioTimeNumber) + ":00");
                        }
                        //Setting the time left in the timer
                        mTimeLeftInMillis = START_TIME_IN_MILLIS * cardioTimeNumber;
                    }
                    //If the user picked to use sets and repetitions for the cardio exercises (cardioUsesTime == false)
                    else {
                        exerciseTimeLayout.setVisibility(GONE);
                        exerciseSetsAndRepetitionsLayout.setVisibility(View.VISIBLE);
                        exerciseSets.setText(String.valueOf(cardioSetsNumber));
                        exerciseReps.setText(String.valueOf(cardioRepsNumber));
                    }
                }
                //If the current component is weight training
                if (currentComponent.equals("Component 3: Weight Training")) {
                    componentTitle.setText(currentComponent);
                    //Displaying the index of the weight training exercises, so the user can keep track of the current exercise
                    exerciseCountString = "Exercise: " + String.valueOf(currentExercise + 1) + "/" + String.valueOf(weightTrainingExercises.size());
                    exerciseCount.setText(exerciseCountString);
                    //If the current exercise has not reached the end of the list of weight training exercises
                    if ((currentExercise < (weightTrainingExercises.size()))) {
                        ShowExercise(weightTrainingExercises.get(currentExercise));
                        currentExercise++;
                    }
                    //If the current exercise has reached the end of the list of weight training exercises, change the current component to core
                    else if (currentExercise == (weightTrainingExercises.size())) {
                        currentExercise = 0;
                        currentComponent = "Component 4: Core";
                    }
                    //If the user picked to use time in minutes for the weight training exercises
                    if (wtUsesTime) {
                        exerciseTimeLayout.setVisibility(View.VISIBLE);
                        exerciseSetsAndRepetitionsLayout.setVisibility(GONE);
                        //Adjusting the way the minutes are displayed in the text view based on the amount of minutes picked by the user
                        if (wtTimeNumber < 10) {
                            exerciseTime.setText("0" + String.valueOf(wtTimeNumber) + ":00");
                        }
                        if (wtTimeNumber >= 10) {
                            exerciseTime.setText(String.valueOf(wtTimeNumber) + ":00");
                        }
                        //Setting the time left in the timer
                        mTimeLeftInMillis = START_TIME_IN_MILLIS * wtTimeNumber;
                    }
                    //If the user picked to use sets and repetitions for the weight training exercises (wtUsesTime == false)
                    else {
                        exerciseTimeLayout.setVisibility(GONE);
                        exerciseSetsAndRepetitionsLayout.setVisibility(View.VISIBLE);
                        exerciseSets.setText(String.valueOf(wtSetsNumber));
                        exerciseReps.setText(String.valueOf(wtRepsNumber));
                    }
                }
                //If the current component is core
                if (currentComponent.equals("Component 4: Core")) {
                    componentTitle.setText(currentComponent);
                    //Displaying the index of the cardio exercises, so the user can keep track of the current exercise
                    exerciseCountString = "Exercise: " + String.valueOf(currentExercise + 1) + "/" + String.valueOf(coreExercises.size());
                    exerciseCount.setText(exerciseCountString);
                    //If the current exercise has not reached the end of the list of core exercises
                    if ((currentExercise < (coreExercises.size()))) {
                        ShowExercise(coreExercises.get(currentExercise));
                        currentExercise++;
                    }
                    //If the current exercise has reached the end of the list of core exercises, change the current component to stretching
                    else if (currentExercise == (coreExercises.size())) {
                        currentExercise = 0;
                        currentComponent = "Component 5: Stretching";
                    }
                    //If the user picked to use time in minutes for the core exercises
                    if (coreUsesTime) {
                        exerciseTimeLayout.setVisibility(View.VISIBLE);
                        exerciseSetsAndRepetitionsLayout.setVisibility(GONE);
                        //Adjusting the way the minutes are displayed in the text view based on the amount of minutes picked by the user
                        if (coreTimeNumber < 10) {
                            exerciseTime.setText("0" + String.valueOf(coreTimeNumber) + ":00");
                        }
                        if (coreTimeNumber >= 10) {
                            exerciseTime.setText(String.valueOf(coreTimeNumber) + ":00");
                        }
                        //Setting the time left in the timer
                        mTimeLeftInMillis = START_TIME_IN_MILLIS * coreTimeNumber;
                    }
                    //If the user picked to use sets and repetitions for the core exercises (coreUsesTime == false)
                    else {
                        exerciseTimeLayout.setVisibility(GONE);
                        exerciseSetsAndRepetitionsLayout.setVisibility(View.VISIBLE);
                        exerciseSets.setText(String.valueOf(coreSetsNumber));
                        exerciseReps.setText(String.valueOf(coreRepsNumber));
                    }
                }
                //If the current component is stretching
                if (currentComponent.equals("Component 5: Stretching")) {
                    componentTitle.setText(currentComponent);
                    //Displaying the index of the cardio exercises, so the user can keep track of the current exercise
                    exerciseCountString = "Exercise: " + String.valueOf(currentExercise + 1) + "/" + String.valueOf(stretchingExercises.size());
                    exerciseCount.setText(exerciseCountString);
                    //If the current exercise has not reached the end of the list of stretching exercises
                    if ((currentExercise < (stretchingExercises.size()))) {
                        ShowExercise(stretchingExercises.get(currentExercise));
                        currentExercise++;
                    }
                    //If the current exercise has reached the end of the list of stretching exercises, finish the workout
                    else if (currentExercise == (stretchingExercises.size())) {
                        currentExercise = 0;
                        workoutLayout.setVisibility(GONE);
                        workoutFeedbackLayout.setVisibility(View.VISIBLE);
                        SetFeedbackReportFields();
                        // do stuff here
                    }
                    exerciseTimeLayout.setVisibility(View.VISIBLE);
                    exerciseSetsAndRepetitionsLayout.setVisibility(GONE);
                    exerciseTime.setText("01:00");
                    mTimeLeftInMillis = START_TIME_IN_MILLIS * 1;
                }
            }
        });

        cardioEasyExercisesET = (EditText) findViewById(R.id.cardio_easy_exercises);
        cardioMediumExercisesET = (EditText) findViewById(R.id.cardio_medium_exercises);
        cardioDifficultExercisesET = (EditText) findViewById(R.id.cardio_difficult_exercises);
        cardioSetsET = (EditText) findViewById(R.id.cardio_sets);
        cardioRepetitionsET = (EditText) findViewById(R.id.cardio_repetitions);
        cardioMinutesET = (EditText) findViewById(R.id.cardio_minutes);
        weightTrainingEasyExercisesET = (EditText) findViewById(R.id.weight_training_easy_exercises);
        weightTrainingMediumExercisesET = (EditText) findViewById(R.id.weight_training_medium_exercises);
        weightTrainingDifficultExercisesET = (EditText) findViewById(R.id.weight_training_difficult_exercises);
        weightTrainingSetsET = (EditText) findViewById(R.id.weight_training_sets);
        weightTrainingRepetitionsET = (EditText) findViewById(R.id.weight_training_repetitions);
        weightTrainingMinutesET = (EditText) findViewById(R.id.weight_training_minutes);
        coreEasyExercisesET = (EditText) findViewById(R.id.core_easy_exercises);
        coreMediumExercisesET = (EditText) findViewById(R.id.core_medium_exercises);
        coreDifficultExercisesET = (EditText) findViewById(R.id.core_difficult_exercises);
        coreSetsET = (EditText) findViewById(R.id.core_sets);
        coreRepetitionsET = (EditText) findViewById(R.id.core_repetitions);
        coreMinutesET = (EditText) findViewById(R.id.core_minutes);
        stretchingEasyExercisesTV = (TextView) findViewById(R.id.stretching_easy_exercises);
        stretchingMediumExercisesTV = (TextView) findViewById(R.id.stretching_medium_exercises);
        stretchingDifficultExercisesTV = (TextView) findViewById(R.id.stretching_difficult_exercises);
        stretchingMinutesET = (EditText) findViewById(R.id.stretching_minutes);
        feedbackLevelOfExperienceTV = (TextView) findViewById(R.id.feedback_level_of_experience);
        feedbackWorkoutDifficultyTV = (TextView) findViewById(R.id.feedback_workout_difficulty);
        feedbackWorkoutBodyAreaTargetTV = (TextView) findViewById(R.id.feedback_body_area_target);
        workoutFeedbackLayout = (LinearLayout) findViewById(R.id.workout_feedback);
    }

    /**
     * Displays the exercises' image and name on the workout activity workout layout
     *
     * @param exercise Exercise object whose image and name to be displayed
     */
    private void ShowExercise(Exercise exercise) {
        exerciseImage.setImageResource(exercise.getIcon());
        exerciseName.setText(exercise.name);
    }

    /**
     * Processes all the user's personal data needed to create the workout routine
     */
    public void ProcessUserPersonalData() {
        //Setting up the temporal variables needed to store the user's personalised parameters
        int levelOfExperienceID = 0;
        int goalID = 0;
        int cardioTime = 0;
        int weightTrainingTime = 0;
        int coreTime = 0;
        //Setting up the variables based on the level of experience and the goal for using the application
        if (levelOfExperience.equals("Beginner")) {
            levelOfExperienceID = beginnerID;
        } else if (levelOfExperience.equals("Intermediate")) {
            levelOfExperienceID = intermediateID;
        } else if (levelOfExperience.equals("Advanced")) {
            levelOfExperienceID = advancedID;
        } else if (levelOfExperience.equals("Professional")) {
            levelOfExperienceID = professionalID;
        }
        if (goal.equals("Gain strength")) {
            goalID = gainStrengthID;
        } else if (goal.equals("Lose weight")) {
            goalID = loseWeightID;
        } else if (goal.equals("Be fit")) {
            goalID = beFitID;
        }
        //Setting up the assigned time for each component based the goal for using the application and the level of experience of the user
        cardioTime = cardioTimeValues[goalID][levelOfExperienceID];
        weightTrainingTime = weightTrainingTimeValues[goalID][levelOfExperienceID];
        coreTime = coreTimeValues[goalID][levelOfExperienceID];
        //Setting up the number of exercises for each component, by removing one fifth of each assigned time dedicated for resting (every two exercises, each of them lasting at least a minute, half a minute is dedicated to resting)
        cardioAmountOfExercises = cardioTime - (cardioTime / 5);
        weightTrainingAmountOfExercises = weightTrainingTime - (weightTrainingTime / 5);
        coreAmountOfExercises = coreTime - (coreTime / 5);
        stretchingAmountOfExercises = stretchingTimeValue;
    }

    /**
     * This method gets exercises from the database and stores them in the respective array list based on the following parameters:
     *
     * @param exercise_type                     The type of exercise (cardio, strength or flexibility)
     * @param body_part                         The body part targeted by the exercise (total body for cardio, lower body/upper body/total body and core for strength and total body for flegibility)
     * @param workoutAmountOfEasyExercises      Number of easy exercises needed
     * @param workoutAmountOfMediumExercises    Number of medium exercises needed
     * @param workoutAmountOfDifficultExercises Number of difficult exercises needed
     */
    public void GetPersonalisedExercises(String exercise_type, String body_part, int workoutAmountOfEasyExercises, int workoutAmountOfMediumExercises, int workoutAmountOfDifficultExercises) {
        //Getting the reference from the database
        DatabaseReference databaseReference = database.getReference("exercises").child(exercise_type).child(body_part);
        currentAmountOfExercises = 0;
        //If the exercise type is cardio, this refers to the cardio exercises
        if (exercise_type.equals("cardio")) {
            currentAmountOfExercises = cardioAmountOfExercises;
        }
        //If the exercise type is strength but the body part is not core, this refers to the weight training exercises, as they are located under the strength tag and can target upper body, lower body and total body
        else if (exercise_type.equals("strength") && !body_part.equals("core")) {
            currentAmountOfExercises = weightTrainingAmountOfExercises;
        }
        //If the exercise type is strength and the body part is core, this refers to the core exercises
        else if (exercise_type.equals("strength") && body_part.equals("core")) {
            currentAmountOfExercises = coreAmountOfExercises;
        }
        //If the exercise type is flexibility, this refers to the stretching exercises
        else if (exercise_type.equals("flexibility")) {
            currentAmountOfExercises = stretchingAmountOfExercises;
        }
        int finalCurrentAmountOfExercises = currentAmountOfExercises;
        //Setting the number of easy, medium and difficult exercises needed for the workout routine
        int amountOfEasyExercises = workoutAmountOfEasyExercises * (finalCurrentAmountOfExercises / 4);
        int amountOfMediumExercises = workoutAmountOfMediumExercises * (finalCurrentAmountOfExercises / 4);
        int amountOfDifficultExercises = workoutAmountOfDifficultExercises * (finalCurrentAmountOfExercises / 4);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Setting up array lists to keep track of all the different exercises in the database, so there are no repeated exercises
                ArrayList<Integer> easyExercisesIDs = new ArrayList<Integer>();
                easyExercisesIDs.add(1);
                easyExercisesIDs.add(2);
                easyExercisesIDs.add(3);
                easyExercisesIDs.add(4);
                easyExercisesIDs.add(5);
                ArrayList<Integer> mediumExercisesIDs = new ArrayList<Integer>();
                mediumExercisesIDs.add(1);
                mediumExercisesIDs.add(2);
                mediumExercisesIDs.add(3);
                mediumExercisesIDs.add(4);
                mediumExercisesIDs.add(5);
                ArrayList<Integer> difficultExercisesIDs = new ArrayList<Integer>();
                difficultExercisesIDs.add(1);
                difficultExercisesIDs.add(2);
                difficultExercisesIDs.add(3);
                difficultExercisesIDs.add(4);
                difficultExercisesIDs.add(5);
                /**
                 * Getting all the easy exercises from the database needed for the current array list of exercises
                 */
                for (int j = 0; j < amountOfEasyExercises; j++) {
                    //If the IDs are empty restate them, this will allow repeated easy exercises but will only happen once the first five exercises have been set
                    if (easyExercisesIDs.isEmpty()) {
                        easyExercisesIDs.add(1);
                        easyExercisesIDs.add(2);
                        easyExercisesIDs.add(3);
                        easyExercisesIDs.add(4);
                        easyExercisesIDs.add(5);
                    }
                    Random random1 = new Random();
                    int randomInteger = random1.nextInt(easyExercisesIDs.size());
                    String indexValue = easyExercisesIDs.get(randomInteger).toString();
                    String name = dataSnapshot.child("easy").child(indexValue).child("name").getValue(String.class);
                    easyExercisesIDs.remove(randomInteger);
                    Exercise exercise = SetExerciseFields(name, "easy", exercise_type, body_part);
                    //If the exercise type is cardio, add the exercise to the cardio array list
                    if (exercise_type.equals("cardio")) {
                        cardioExercises.add(exercise);
                    }
                    //If the exercise type is strength and the body part is not core, add the exercise to the weight training array list
                    else if (exercise_type.equals("strength") && !body_part.equals("core")) {
                        weightTrainingExercises.add(exercise);
                    }
                    //If the exercise type is strength and the body part is core, add the exercise to the core array list
                    else if (exercise_type.equals("strength") && body_part.equals("core")) {
                        coreExercises.add(exercise);
                    }
                    //If the exercise type is flexibility, add the exercise to the weight training array list
                    else if (exercise_type.equals("flexibility")) {
                        stretchingExercises.add(exercise);
                    }
                }
                /**
                 * Getting all the medium difficulty exercises from the database needed for the current array list of exercises
                 */
                for (int p = 0; p < amountOfMediumExercises; p++) {
                    //If the exercise type is strength and the body part is total body break, this happens because the database does not contain medium difficulty exercises for total body strength exercises
                    if (exercise_type.equals("strength") && body_part.equals("total_body")) {
                        break;
                    }
                    //If the IDs are empty restate them, this will allow repeated medium exercises but will only happen once the first five exercises have been set
                    if (mediumExercisesIDs.isEmpty()) {
                        mediumExercisesIDs.add(1);
                        mediumExercisesIDs.add(2);
                        mediumExercisesIDs.add(3);
                        mediumExercisesIDs.add(4);
                        mediumExercisesIDs.add(5);
                    }
                    Random random2 = new Random();
                    int randomInteger = random2.nextInt(mediumExercisesIDs.size());
                    String indexValue = mediumExercisesIDs.get(randomInteger).toString();
                    String name = dataSnapshot.child("medium").child(indexValue).child("name").getValue(String.class);
                    mediumExercisesIDs.remove(randomInteger);
                    Exercise exercise = SetExerciseFields(name, "medium", exercise_type, body_part);
                    //If the exercise type is cardio, add the exercise to the cardio array list
                    if (exercise_type.equals("cardio")) {
                        cardioExercises.add(exercise);
                    }
                    //If the exercise type is strength and the body part is not core, add the exercise to the weight training array list
                    else if (exercise_type.equals("strength") && !body_part.equals("core")) {
                        weightTrainingExercises.add(exercise);
                    }
                    //If the exercise type is strength and the body part is core, add the exercise to the core array list
                    else if (exercise_type.equals("strength") && body_part.equals("core")) {
                        coreExercises.add(exercise);
                    }
                    //If the exercise type is flexibility, add the exercise to the weight training array list
                    else if (exercise_type.equals("flexibility")) {
                        stretchingExercises.add(exercise);
                    }
                }
                /**
                 * Getting all the difficult exercises from the database needed for the current array list of exercises
                 */
                for (int q = 0; q < amountOfDifficultExercises; q++) {
                    //If the exercise type is strength and the body part is total body break, this happens because the database does not contain difficult exercises for total body strength exercises
                    if (exercise_type.equals("strength") && body_part.equals("total_body")) {
                        break;
                    }
                    //If the IDs are empty restate them, this will allow repeated difficult exercises but will only happen once the first five exercises have been set
                    if (difficultExercisesIDs.isEmpty()) {
                        difficultExercisesIDs.add(1);
                        difficultExercisesIDs.add(2);
                        difficultExercisesIDs.add(3);
                        difficultExercisesIDs.add(4);
                        difficultExercisesIDs.add(5);
                    }
                    Random random3 = new Random();
                    //System.out.println("Index value    " + difficultExercisesIDs.size());
                    int randomInteger = random3.nextInt(difficultExercisesIDs.size());
                    //System.out.println("Index value    " + randomInteger);
                    String indexValue = difficultExercisesIDs.get(randomInteger).toString();
                    //System.out.println("Index value" + indexValue);
                    String name = dataSnapshot.child("difficult").child(indexValue).child("name").getValue(String.class);
                    //String name = dataSnapshot.child("easy").child("3").child("name").getValue(String.class);
                    difficultExercisesIDs.remove(randomInteger);
                    //System.out.println("diffy exercise: " + name);
                    Exercise exercise = SetExerciseFields(name, "difficult", exercise_type, body_part);
                    //If the exercise type is cardio, add the exercise to the cardio array list
                    if (exercise_type.equals("cardio")) {
                        cardioExercises.add(exercise);
                    }
                    //If the exercise type is strength and the body part is not core, add the exercise to the weight training array list
                    else if (exercise_type.equals("strength") && !body_part.equals("core")) {
                        weightTrainingExercises.add(exercise);
                    }
                    //If the exercise type is strength and the body part is core, add the exercise to the core array list
                    else if (exercise_type.equals("strength") && body_part.equals("core")) {
                        coreExercises.add(exercise);
                    }
                    //If the exercise type is flexibility, add the exercise to the weight training array list
                    else if (exercise_type.equals("flexibility")) {
                        stretchingExercises.add(exercise);
                    }
                }
                //Notifying adapters of the data changed based on the type of exercises added
                if (exercise_type.equals("cardio")) {
                    cardioExercisesAdapter.notifyDataSetChanged();
                }
                if (exercise_type.equals("strength") && !body_part.equals("core")) {
                    weightTrainingExercisesAdapter.notifyDataSetChanged();
                }
                if (exercise_type.equals("strength") && body_part.equals("core")) {
                    coreExercisesAdapter.notifyDataSetChanged();
                }
                if (exercise_type.equals("flexibility")) {
                    stretchingExercisesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Exercise constructor, it takes as parameters all the exercise fields needed to create a new exercise and it returns the created exercise
     *
     * @param name       Name of the exercise
     * @param difficulty Difficulty of the exercise
     * @param type       Type of exercise (strength, cardio, flexibility)
     * @param body_part  Body part the exercise targets (upper body, lower body, total body or core)
     * @return
     */
    public Exercise SetExerciseFields(String name, String difficulty, String type, String body_part) {
        Exercise exercise = new Exercise();
        String imageFileName = name; //This is the image file name
        String PACKAGE_NAME = this.getPackageName();
        int imgId = this.getResources().getIdentifier(PACKAGE_NAME + ":drawable/" + imageFileName, null, null);
        exercise.setIcon(imgId);
        exercise.setName(replace(name));
        exercise.setDifficulty(replace(difficulty));
        exercise.setType(replace(type));
        exercise.setBody_part(replace(body_part));
        return exercise;
    }

    /**
     * Starts and resumes the counter.
     */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setBackgroundResource(R.drawable.ic_start);
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();
        mTimerRunning = true;
        mButtonStartPause.setBackgroundResource(R.drawable.ic_pause);
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    /**
     * Pauses the counter.
     */
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setBackgroundResource(R.drawable.ic_start);
        mButtonReset.setVisibility(View.VISIBLE);
    }

    /**
     * Resets the counter.
     */
    private void resetTimer() {
        if (cardioUsesTime && currentComponent.equals("Component 2: Cardio")) {
            mTimeLeftInMillis = START_TIME_IN_MILLIS * cardioTimeNumber;
        }
        if (wtUsesTime && currentComponent.equals("Component 3: Weight Training")) {
            mTimeLeftInMillis = START_TIME_IN_MILLIS * wtTimeNumber;
        }
        if (coreUsesTime && currentComponent.equals("Component 4: Core")) {
            mTimeLeftInMillis = START_TIME_IN_MILLIS * coreTimeNumber;
        }
        if (currentComponent.equals("Component 5: Stretching")) {
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
        }
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the count down text every second
     */
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    /**
     * Capitalizes the input string and replaces the under score values from the input string to space values
     * I used the following source to implement this method:
     * https://codereview.stackexchange.com/questions/188996/replace-any-number-of-consecutive-underscores-with-a-single-space
     *
     * @param input input string
     * @return returns changed string
     */
    public static String replace(String input) {
        String spacesReplaced = replace('_', ' ', input);
        String finalName = spacesReplaced.substring(0, 1).toUpperCase() + spacesReplaced.substring(1);
        return finalName;
    }

    /**
     * Replaces the under score values from the input string to space values
     * I used the following source to implement this method:
     * https://codereview.stackexchange.com/questions/188996/replace-any-number-of-consecutive-underscores-with-a-single-space
     *
     * @param oldDelim delimiter to be replaced
     * @param newDelim delimiter replaced by
     * @param input    input string
     * @return returns changed string
     */
    public static String replace(char oldDelim, char newDelim, String input) {
        boolean wasOldDelim = false;
        int o = 0;
        char[] buf = input.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            assert (o <= i);
            if (buf[i] == oldDelim) {
                if (wasOldDelim) {
                    continue;
                }
                wasOldDelim = true;
                buf[o++] = newDelim;
            } else {
                wasOldDelim = false;
                buf[o++] = buf[i];
            }
        }
        return new String(buf, 0, o);
    }

    /**
     * This method sets the default values of the feedback form fields to be the values set by the user at the beginning of the workout routine
     */
    private void SetFeedbackReportFields() {
        //Setting parameters that will store the default values of the cardio exercises
        int cardioEasyExercises = amountEasyExercises * (cardioAmountOfExercises / 4);
        int cardioMediumExercises = amountMediumExercises * (cardioAmountOfExercises / 4);
        int cardioDifficultExercises = amountDifficultExercises * (cardioAmountOfExercises / 4);
        //Setting the text of the feedback form cardio fields to the parameters
        cardioEasyExercisesET.setText(String.valueOf(cardioEasyExercises));
        cardioMediumExercisesET.setText(String.valueOf(cardioMediumExercises));
        cardioDifficultExercisesET.setText(String.valueOf(cardioDifficultExercises));
        if (cardioUsesTime) {
            cardioMinutesET.setText(String.valueOf(cardioTimeNumber));
        } else {
            cardioSetsET.setText(String.valueOf(cardioSetsNumber));
            cardioRepetitionsET.setText(String.valueOf(cardioRepsNumber));
        }
        //Setting parameters that will store the default values of the weight training exercises
        int weightTrainingEasyExercises = amountEasyExercises * (weightTrainingAmountOfExercises / 4);
        int weightTrainingMediumExercises = amountMediumExercises * (weightTrainingAmountOfExercises / 4);
        int weightTrainingDifficultExercises = amountDifficultExercises * (weightTrainingAmountOfExercises / 4);
        //Setting the text of the feedback form weight training fields to the parameters
        weightTrainingEasyExercisesET.setText(String.valueOf(weightTrainingEasyExercises));
        weightTrainingMediumExercisesET.setText(String.valueOf(weightTrainingMediumExercises));
        weightTrainingDifficultExercisesET.setText(String.valueOf(weightTrainingDifficultExercises));
        if (wtUsesTime) {
            weightTrainingMinutesET.setText(String.valueOf(wtTimeNumber));
        } else {
            weightTrainingSetsET.setText(String.valueOf(wtSetsNumber));
            weightTrainingRepetitionsET.setText(String.valueOf(wtRepsNumber));
        }
        //Setting parameters that will store the default values of the core exercises
        int coreEasyExercises = amountEasyExercises * (coreAmountOfExercises / 4);
        int coreMediumExercises = amountMediumExercises * (coreAmountOfExercises / 4);
        int coreDifficultExercises = amountDifficultExercises * (coreAmountOfExercises / 4);
        //Setting the text of the feedback form core fields to the parameters
        coreEasyExercisesET.setText(String.valueOf(coreEasyExercises));
        coreMediumExercisesET.setText(String.valueOf(coreMediumExercises));
        coreDifficultExercisesET.setText(String.valueOf(coreDifficultExercises));
        if (coreUsesTime) {
            coreMinutesET.setText(String.valueOf(coreTimeNumber));
        } else {
            coreSetsET.setText(String.valueOf(coreSetsNumber));
            coreRepetitionsET.setText(String.valueOf(coreRepsNumber));
        }
        //Setting parameters that will store the default values of the stretching exercises
        int stretchingEasyExercises = amountEasyExercises * (stretchingAmountOfExercises / 4);
        int stretchingMediumExercises = amountMediumExercises * (stretchingAmountOfExercises / 4);
        int stretchingDifficultExercises = amountDifficultExercises * (stretchingAmountOfExercises / 4);
        //Setting the text of the feedback form stretching fields to the parameters
        stretchingEasyExercisesTV.setText(String.valueOf(stretchingEasyExercises));
        stretchingMediumExercisesTV.setText(String.valueOf(stretchingMediumExercises));
        stretchingDifficultExercisesTV.setText(String.valueOf(stretchingDifficultExercises));
        stretchingMinutesET.setText(String.valueOf(stretchingTimeValue));
        feedbackLevelOfExperienceTV.setText(levelOfExperience);
        feedbackWorkoutDifficultyTV.setText(replace(workoutDifficulty));
        feedbackWorkoutBodyAreaTargetTV.setText(replace(workoutBodyArea));
    }

    /**
     * This method saves the changed values of the feedback form to the database
     */
    private void SaveWorkoutInformationToDatabase() {
        //Getting the current year
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        //Getting the current month, as the month list starts with 0 for January, to get the current month one is added to the instance
        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1);
        //String to contain the current day
        String day = "";
        //Getting a calendar instance
        Calendar calendar = Calendar.getInstance();
        //Getting the  current day of the week
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //Switch case used to set the current day based on the current day of the week obtained from the calendar
        switch (currentDayOfWeek) {
            case Calendar.SUNDAY:
                day = "Sunday";
                break;
            case Calendar.MONDAY:
                day = "Monday";
                break;
            case Calendar.TUESDAY:
                day = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                day = "Wednesday";
                break;
            case Calendar.THURSDAY:
                day = "Thursday";
                break;
            case Calendar.FRIDAY:
                day = "Friday";
                break;
            case Calendar.SATURDAY:
                day = "Saturday";
                break;
        }
        //Getting the current week of the month
        String weekOfMonth = String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_MONTH));
        //Getting Firebase instance
        database = FirebaseDatabase.getInstance();
        //Setting database reference
        DatabaseReference databaseReference = database.getReference("users");
        //Getting the user's personal ID
        String personalID = GetPersonalID(this);
        //Getting the data from the cardio exercises input fields from the feedback form and storing them into the database
        String cardioEasyExercises = String.valueOf(cardioEasyExercisesET.getText());
        String cardioMediumExercises = String.valueOf(cardioMediumExercisesET.getText());
        String cardioDifficultExercises = String.valueOf(cardioDifficultExercisesET.getText());
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_easy_exercises").setValue(cardioEasyExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_medium_exercises").setValue(cardioMediumExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_difficult_exercises").setValue(cardioDifficultExercises);
        String cardioUsesTimeBooleanValue;
        String cardioSets;
        String cardioReps;
        String cardioMinutes;
        //If the cardio component uses time, save the minutes in the database, if not save the number of sets and repetitions in the database
        if (cardioUsesTime) {
            cardioUsesTimeBooleanValue = "true";
            cardioMinutes = String.valueOf(Integer.valueOf(String.valueOf(cardioMinutesET.getText())) * cardioAmountOfExercises);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_uses_time").setValue(cardioUsesTimeBooleanValue);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_minutes").setValue(cardioMinutes);
        } else {
            cardioUsesTimeBooleanValue = "false";
            cardioSets = String.valueOf(Integer.valueOf(String.valueOf(cardioSetsET.getText())) * cardioAmountOfExercises);
            cardioReps = String.valueOf(Integer.valueOf(String.valueOf(cardioRepetitionsET.getText())) * cardioAmountOfExercises);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_uses_time").setValue(cardioUsesTimeBooleanValue);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_sets").setValue(cardioSets);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("cardio_repetitions").setValue(cardioReps);
        }
        //Getting the data from the weight training exercises input fields from the feedback form and storing them into the database
        String weightTrainingEasyExercises = String.valueOf(weightTrainingEasyExercisesET.getText());
        String weightTrainingMediumExercises = String.valueOf(weightTrainingMediumExercisesET.getText());
        String weightTrainingDifficultExercises = String.valueOf(weightTrainingDifficultExercisesET.getText());
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_easy_exercises").setValue(weightTrainingEasyExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_medium_exercises").setValue(weightTrainingMediumExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_difficult_exercises").setValue(weightTrainingDifficultExercises);
        String weightTrainingUsesTimeBooleanValue;
        String weightTrainingSets;
        String weightTrainingReps;
        String weightTrainingMinutes;
        //If the weight training component uses time, save the minutes in the database, if not save the number of sets and repetitions in the database
        if (wtUsesTime) {
            weightTrainingUsesTimeBooleanValue = "true";
            weightTrainingMinutes = String.valueOf(Integer.valueOf(String.valueOf(weightTrainingMinutesET.getText())) * weightTrainingAmountOfExercises);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_uses_time").setValue(weightTrainingUsesTimeBooleanValue);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_minutes").setValue(weightTrainingMinutes);
        } else {
            weightTrainingUsesTimeBooleanValue = "false";
            weightTrainingSets = String.valueOf(Integer.valueOf(String.valueOf(weightTrainingSetsET.getText())) * weightTrainingAmountOfExercises);
            weightTrainingReps = String.valueOf(Integer.valueOf(String.valueOf(weightTrainingRepetitionsET.getText())) * weightTrainingAmountOfExercises);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_uses_time").setValue(weightTrainingUsesTimeBooleanValue);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_sets").setValue(weightTrainingSets);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("weight_training_repetitions").setValue(weightTrainingReps);
        }
        //Getting the data from the core exercises input fields from the feedback form and storing them into the database
        String coreEasyExercises = String.valueOf(coreEasyExercisesET.getText());
        String coreMediumExercises = String.valueOf(coreMediumExercisesET.getText());
        String coreDifficultExercises = String.valueOf(coreDifficultExercisesET.getText());
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_easy_exercises").setValue(coreEasyExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_medium_exercises").setValue(coreMediumExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_difficult_exercises").setValue(coreDifficultExercises);
        String coreUsesTimeBooleanValue;
        String coreSets;
        String coreReps;
        String coreMinutes;
        //If the core component uses time, save the minutes in the database, if not save the number of sets and repetitions in the database
        if (coreUsesTime) {
            coreUsesTimeBooleanValue = "true";
            coreMinutes = String.valueOf(Integer.valueOf(String.valueOf(coreMinutesET.getText())) * coreAmountOfExercises);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_uses_time").setValue(coreUsesTimeBooleanValue);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_minutes").setValue(coreMinutes);
        } else {
            coreUsesTimeBooleanValue = "false";
            coreSets = String.valueOf(Integer.valueOf(String.valueOf(coreSetsET.getText())) * coreAmountOfExercises);
            coreReps = String.valueOf(Integer.valueOf(String.valueOf(coreRepetitionsET.getText())) * coreAmountOfExercises);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_uses_time").setValue(coreUsesTimeBooleanValue);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_sets").setValue(coreSets);
            databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("core_repetitions").setValue(coreReps);
        }
        String stretchingEasyExercises = String.valueOf(stretchingEasyExercisesTV.getText());
        String stretchingMediumExercises = String.valueOf(stretchingMediumExercisesTV.getText());
        String stretchingDifficultExercises = String.valueOf(stretchingDifficultExercisesTV.getText());
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("stretching_easy_exercises").setValue(stretchingEasyExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("stretching_medium_exercises").setValue(stretchingMediumExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("stretching_difficult_exercises").setValue(stretchingDifficultExercises);
        String stretchingMinutes = String.valueOf(stretchingTimeValue * stretchingAmountOfExercises);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("stretching_minutes").setValue(stretchingMinutes);
        //Saving the workout body area target and workout difficulty to the database
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("body_area_target").setValue(workoutBodyArea);
        databaseReference.child(personalID).child("workouts").child(year).child(month).child(weekOfMonth).child(day).child("workout_difficulty").setValue(workoutDifficulty);
    }

    /**
     * This method is used to retrieve the user's personal ID from the device's storage
     *
     * @param view The fragment's view
     * @return It returns the user's personal ID as a String
     */
    public String GetPersonalID(WorkoutActivity view) {
        //Try reading the contents of the file containing the user's personal ID
        try {
            //Creating a file input stream, using user_data.txt as the input file
            FileInputStream fileIn = view.openFileInput("user_data.txt");
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            //Setting a character array to store the first 100 characters of the input stream
            char[] inputBuffer = new char[100];
            //String used to store the user's personal ID
            String userPersonalID = "";
            //String array used to store the different parts of the input stream
            String[] strArray = new String[100];
            int charRead;
            //While there is something to read from the file
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                //A semicolon is used to split the read string from the input buffer
                strArray = readstring.split(";");
            }
            //Close the input reader
            InputRead.close();
            //Set the user personal ID to the second element of the string array
            userPersonalID = strArray[1];
            //Return the the user's personal ID
            return userPersonalID;
        } catch (Exception e) {
            e.printStackTrace();
            //If the file could not be read, return "String not found"
            return "String not found";
        }
    }
}