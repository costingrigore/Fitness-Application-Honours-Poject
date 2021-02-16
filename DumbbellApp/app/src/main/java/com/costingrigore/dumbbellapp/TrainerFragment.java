package com.costingrigore.dumbbellapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainerFragment extends Fragment {

    TextView textView;
    Button button;
    String name;
    private int mColumnCount = 1;

    public TrainerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TrainerFragment newInstance() {
        TrainerFragment fragment = new TrainerFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trainer, container, false);
        button = (Button) view.findViewById(R.id.button);
        WorkoutRoutineCreationAlgorithm WRCA = new WorkoutRoutineCreationAlgorithm(levelOfExperience,goal,dayType,view);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                WRCA.ProcessUserPersonalData();
                WRCA.GetPersonalisedCardioExercises();
            }
        });


/**        WorkoutRoutineCreationAlgorithm WRCA = new WorkoutRoutineCreationAlgorithm("Intermediate","Be fit","upper_body", view);
        WRCA.ProcessUserPersonalData();
        ArrayList<Exercise> cardioExercises = WRCA.GetPersonalisedCardioExercises();
**/
        return view;
    }

    private String levelOfExperience = "Intermediate";
    private String goal = "Be fit";
    private String dayType = "upper_body";


    private View view;

}
/**
    FirebaseDatabase database;
    private int beginnerID = 0;
    private int intermediateID = 1;
    private int advancedID = 2;
    private int professionalID = 3;

    private int beFitID = 0;
    private int loseWeightID = 1;
    private int gainStrengthID = 2;


    public ArrayList<Exercise> cardioExercises = new ArrayList<>();
    public ArrayList<Exercise> weightTrainingExercises= new ArrayList<>();
    public ArrayList<Exercise> coreExercises= new ArrayList<>();
    public ArrayList<Exercise> stretchingExercises= new ArrayList<>();

    int cardioAmountOfExercises;
    int weightTrainingAmountOfExercises;
    int coreAmountOfExercises;

    public ArrayList<Exercise> getCardioExercises() {
        return cardioExercises;
    }
    public ArrayList<Exercise> getWeightTrainingExercises() {
        return weightTrainingExercises;
    }
    public ArrayList<Exercise> getCoreExercises() {
        return coreExercises;
    }
    public ArrayList<Exercise> getStretchingExercises() {
        return stretchingExercises;
    }


    private int[][] cardioTimeValues =
            {
                    {15, 20, 25, 30},
                    {20, 25, 30, 30},
                    {15, 15, 20, 25}
            };
    private int[][] weightTrainingTimeValues =
            {
                    {10, 15, 15, 20},
                    {10, 10, 15, 20},
                    {20, 20, 25, 30}
            };
    private int[][] coreTimeValues =
            {
                    {10, 10, 15, 15},
                    {5, 5, 10, 10},
                    {10, 10, 15, 20}
            };

  /**  public WorkoutRoutineCreationAlgorithm(String levelOfExperience, String goal, String dayType, View view) {
        this.levelOfExperience = levelOfExperience;
        this.goal = goal;
        this.dayType = dayType;
        this.view = view;
    }

    public void ProcessUserPersonalData(){
        int levelOfExperienceID = 0;
        int goalID = 0;
        int cardioTime = 0;
        int weightTrainingTime = 0;
        int coreTime = 0;
        if(levelOfExperience.equals("Beginner")){
            levelOfExperienceID = beginnerID;
        }
        else if(levelOfExperience.equals("Intermediate")){
            levelOfExperienceID = intermediateID;
        }
        else if(levelOfExperience.equals("Advanced")){
            levelOfExperienceID = advancedID;
        }
        else if(levelOfExperience.equals("Professional")){
            levelOfExperienceID = professionalID;
        }
        if(goal.equals("Gain strength")){
            goalID = gainStrengthID;
        }
        else if(goal.equals("Lose weight")){
            goalID = loseWeightID;
        }
        else if(goal.equals("Be fit")){
            goalID = beFitID;
        }
        cardioTime = cardioTimeValues[goalID][levelOfExperienceID];
        weightTrainingTime = weightTrainingTimeValues[goalID][levelOfExperienceID];
        coreTime = coreTimeValues[goalID][levelOfExperienceID];
        System.out.println("User data processed: " + cardioTime +" , " + weightTrainingTime+ " , " + coreTime);
        cardioAmountOfExercises = cardioTime - (cardioTime / 5);
        weightTrainingAmountOfExercises = weightTrainingTime - (weightTrainingTime / 5);
        coreAmountOfExercises = coreTime - (coreTime / 5);
        //weightTrainingExercises = GetPersonalisedWeightTrainingExercises(weightTrainingAmountOfExercises);
        //coreExercises = GetPersonalisedCoreExercises(coreAmountOfExercises);

    }

    public void GetPersonalisedCardioExercises(){
        Context context = view.getContext();
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(llm);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("exercises").child("cardio").child("total_body");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // for cardio
                String exercise_type = "cardio";
                String body_part = "total_body";
                String exerciseDifficultyState = "easy";
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
                for(int i = 0; i< cardioAmountOfExercises; i++)
                {
                    if(easyExercisesIDs.isEmpty())
                    {
                        easyExercisesIDs.add(1);
                        easyExercisesIDs.add(2);
                        easyExercisesIDs.add(3);
                        easyExercisesIDs.add(4);
                        easyExercisesIDs.add(5);
                    }
                    else if(mediumExercisesIDs.isEmpty())
                    {
                        mediumExercisesIDs.add(1);
                        mediumExercisesIDs.add(2);
                        mediumExercisesIDs.add(3);
                        mediumExercisesIDs.add(4);
                        mediumExercisesIDs.add(5);
                    }
                    else if(difficultExercisesIDs.isEmpty())
                    {
                        difficultExercisesIDs.add(1);
                        difficultExercisesIDs.add(2);
                        difficultExercisesIDs.add(3);
                        difficultExercisesIDs.add(4);
                        difficultExercisesIDs.add(5);
                    }
                    if(exerciseDifficultyState.equals("easy"))
                    {
                        //Random random1 = new Random();
                        //int randomInteger = random1.nextInt(easyExercises.size());
                        //String indexValue = easyExercises.get(randomInteger).toString();
                        //String name = dataSnapshot.child("easy").child(indexValue).child("name").getValue(String.class);
                        Random random1 = new Random();
                        int randomInteger = random1.nextInt(easyExercisesIDs.size());
                        String indexValue = easyExercisesIDs.get(randomInteger).toString();
                        String name = dataSnapshot.child("easy").child(indexValue).child("name").getValue(String.class);
                        //String name = dataSnapshot.child("easy").child("1").child("name").getValue(String.class);
                        System.out.println("eazy exercise: " + name);
                        easyExercisesIDs.remove(randomInteger);
                        exerciseDifficultyState = "medium";
                        Exercise exercise = SetExerciseFields(name, exerciseDifficultyState,exercise_type,body_part);
                        cardioExercises.add(exercise);
                    }
                    else if(exerciseDifficultyState.equals("medium"))
                    {
                        Random random2 = new Random();
                        int randomInteger = random2.nextInt(mediumExercisesIDs.size());
                        String indexValue = mediumExercisesIDs.get(randomInteger).toString();
                        String name = dataSnapshot.child("medium").child(indexValue).child("name").getValue(String.class);
                        //String name = dataSnapshot.child("easy").child("2").child("name").getValue(String.class);
                        mediumExercisesIDs.remove(randomInteger);
                        System.out.println("medium exercise: " + name);
                        exerciseDifficultyState = "difficult";
                        Exercise exercise = SetExerciseFields(name, exerciseDifficultyState,exercise_type,body_part);
                        cardioExercises.add(exercise);
                    }
                    else if(exerciseDifficultyState.equals("difficult"))
                    {
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
                        exerciseDifficultyState = "easy";
                        Exercise exercise = SetExerciseFields(name, exerciseDifficultyState,exercise_type,body_part);
                        cardioExercises.add(exercise);
                    }
                }
                System.out.println("Big potato1" + cardioExercises.size());
                textView = (TextView) view.findViewById(R.id.name);
                textView.setText(cardioExercises.get(3).name);
                /**for (DataSnapshot snapshot_difficulty : dataSnapshot.getChildren()) {
                 difficulty = snapshot_difficulty.getKey();
                 for (DataSnapshot snapshot_exercise_id : snapshot_difficulty.getChildren()) {
                 for (DataSnapshot snapshot_exercise_name : snapshot_exercise_id.getChildren()) {

                 String name = snapshot_exercise_name.getValue(String.class);
                 Exercise exercise1 = new Exercise();
                 String imageFileName = name; //  this is image file name
                 String PACKAGE_NAME = view.getContext().getPackageName();
                 int imgId = view.getResources().getIdentifier(PACKAGE_NAME + ":drawable/" + imageFileName, null, null);
                 exercise1.setIcon(imgId);
                 name = replace(name);
                 exercise1.setName(name);
                 difficulty = replace(difficulty);
                 exercise1.setDifficulty(difficulty);
                 exercise_type = replace(exercise_type);
                 exercise1.setType(exercise_type);
                 body_part = replace(body_part);
                 exercise1.setBody_part(body_part);
                 personalisedCoreExercises.add(exercise1);
                 }
                 }
                 }
                if(!cardioExercises.isEmpty()){
                    final MyExerciseRecyclerViewAdapter adapter = new MyExerciseRecyclerViewAdapter(cardioExercises);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public ArrayList<Exercise> GetPersonalisedWeightTrainingExercises(int weightTrainingAmountOfExercises){
        ArrayList<Exercise> personalisedWeightTrainingExercises = new ArrayList<Exercise>();

        return personalisedWeightTrainingExercises;
    }
    public ArrayList<Exercise> GetPersonalisedCoreExercises(int coreAmountOfExercises){
        ArrayList<Exercise> personalisedCoreExercises = new ArrayList<Exercise>();

        return personalisedCoreExercises;
    }

    public Exercise SetExerciseFields(String name, String difficulty, String type, String body_part){
        Exercise exercise = new Exercise();
        String imageFileName = name; //  this is image file name
        String PACKAGE_NAME = view.getContext().getPackageName();
        int imgId = view.getResources().getIdentifier(PACKAGE_NAME + ":drawable/" + imageFileName, null, null);
        exercise.setIcon(imgId);
        exercise.setName(replace(name));
        exercise.setDifficulty(replace(difficulty));
        exercise.setType(replace(type));
        exercise.setBody_part(replace(body_part));
        return exercise;
    }

    public static String replace(String input) {
        String spacesReplaced = replace('_', ' ', input);
        String finalName = spacesReplaced.substring(0, 1).toUpperCase() + spacesReplaced.substring(1);
        return finalName;
    }

    public static String replace(char oldDelim, char newDelim, String input) {
        boolean wasOldDelim = false;
        int o = 0;
        char[] buf = input.toCharArray();
        for (int i = 0; i < buf.length; i++) {
            assert(o <= i);
            if (buf[i] == oldDelim) {
                if (wasOldDelim) { continue; }
                wasOldDelim = true;
                buf[o++] = newDelim;
            } else {
                wasOldDelim = false;
                buf[o++] = buf[i];
            }
        }
        return new String(buf, 0, o);
    }
}**/