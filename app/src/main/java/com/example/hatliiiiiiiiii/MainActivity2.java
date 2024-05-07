package com.example.hatliiiiiiiiii;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity2 extends AppCompatActivity {

    private Spinner collegeSpinner;
    private Spinner departmentSpinner;
    private Spinner specialtySpinner;
    private Spinner levelSpinner;
    private Spinner sectionSpinner;
    private Spinner groupSpinner;
    private TextView facultyInfoTextView;
    private Button goTo;
    private List<String> departmentIdsList;
    private List<String> specialtyIdsList;
    private List<String> levelIdsList;
    private List<String> sectionIdsList;
    private List<String> groupeIdsList;
    private String groupeId,specialtyId,niveouId,sectionId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        collegeSpinner = findViewById(R.id.college_spinner);
        departmentSpinner = findViewById(R.id.department_spinner);
        specialtySpinner = findViewById(R.id.specialty_spinner);
        levelSpinner = findViewById(R.id.level_spinner);
        sectionSpinner = findViewById(R.id.section_spinner);
        groupSpinner =findViewById(R.id.group_spinner);
        facultyInfoTextView = findViewById(R.id.faculty_info);

        new FetchCollegesTask().execute();

        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCollegeId = String.valueOf(position + 1);
                new FetchDepartmentsTask().execute(selectedCollegeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected
            }
        });

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (departmentIdsList != null && departmentIdsList.size() > position) {
                    String selectedDepartmentId = departmentIdsList.get(position);
                    new FetchSpecialtyTask().execute(selectedDepartmentId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected
            }
        });

        specialtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                specialtyId = specialtyIdsList.get(position);

                new FetchLevelsTask().execute(specialtyId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle nothing selected
            }
        });

        levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String levelCollegeId= levelIdsList.get(position);
                new FetchSectionsTask().execute(levelCollegeId);
                String selectedValue = (String) parentView.getItemAtPosition(position);

                niveouId = selectedValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sectionId=sectionIdsList.get(position);
                new FetchGropeTask().execute(sectionId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupeId =groupeIdsList.get(position);
                // new FetchGropeTask().execute(groupeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        goTo=findViewById(R.id.button);

    }
    public void goTo(View view) {
       Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
        intent.putExtra("specialtyId",specialtyId);
        intent.putExtra("groupId", groupeId);
        intent.putExtra("niveouId", niveouId);
        intent.putExtra("sectionId", sectionId);
        startActivity(intent);
    }
    private class FetchCollegesTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> collegesList = new ArrayList<>();
            try {
                String endpointUrl = "https://num.univ-biskra.dz/psp/pspapi/faculty?key=appmob";
                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);

                for (int i = 0; i < jsonArray.size(); i++) {
                    collegesList.add(jsonArray.get(i).getAsJsonObject().get("name_fac").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return collegesList;
        }

        protected void onPostExecute(List<String> collegesList) {
            if (collegesList != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, collegesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collegeSpinner.setAdapter(adapter);
            }
        }
    }

    private class FetchDepartmentsTask extends AsyncTask<String, Void, Pair<List<String>, List<String>>> {
        @Override
        protected Pair<List<String>, List<String>> doInBackground(String... collegeIds) {
            List<String> departmentsList = new ArrayList<>();
            List<String> departmentsIdList = new ArrayList<>();
            try {
                String collegeId = collegeIds[0];
                String endpointUrl = "https://num.univ-biskra.dz/psp/pspapi/department?faculty=" + collegeId + "&key=appmob";
                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);

                for (int i = 0; i < jsonArray.size(); i++) {
                    departmentsList.add(jsonArray.get(i).getAsJsonObject().get("name_fr").getAsString());
                    departmentsIdList.add(jsonArray.get(i).getAsJsonObject().get("id").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Pair<>(departmentsList, departmentsIdList);
        }

        protected void onPostExecute(Pair<List<String>, List<String>> result) {
            if (result != null)
            {
                List<String> departmentsList = result.first;
                List<String> departmentsIdList = result.second;
                if (departmentsList != null && departmentsIdList != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, departmentsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    departmentSpinner.setAdapter(adapter);
                    departmentIdsList = departmentsIdList;
                }
            }
        }
    }

    private class FetchSpecialtyTask extends AsyncTask<String, Void, Pair<List<String>, List<String>>> {
        @Override
        protected Pair<List<String>, List<String>> doInBackground(String... departmentIds) {
            List<String> specialtiesList = new ArrayList<>();
            List<String> specialtyIdsList = new ArrayList<>();
            try {
                String departmentId = departmentIds[0];
                String endpointUrl = "https://num.univ-biskra.dz/psp/pspapi/specialty?department=" + departmentId + "&semester=2&key=appmob";
                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    specialtiesList.add(jsonArray.get(i).getAsJsonObject().get("Nom_spec").getAsString());
                    specialtyIdsList.add(jsonArray.get(i).getAsJsonObject().get("id_specialty").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Pair<>(specialtiesList, specialtyIdsList);
        }

        protected void onPostExecute(Pair<List<String>, List<String>> result) {
            if (result != null) {
                List<String> specialtiesList = result.first;
                specialtyIdsList = result.second;
                if (specialtiesList != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, specialtiesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    specialtySpinner.setAdapter(adapter);
                }
            }
        }
    }

    private class FetchLevelsTask extends AsyncTask<String, Void, Pair<List<String>, List<String>>> {
        @Override
        protected Pair<List<String>, List<String>> doInBackground(String... specialtyIds) {
            List<String> levelsList = new ArrayList<>();
            List<String> levelsIdsList = new ArrayList<>();
            try {
                String specialtyId = specialtyIds[0];
                String endpointUrl = "https://num.univ-biskra.dz/psp/pspapi/level?specialty=" + specialtyId + "&semester=2&key=appmob";
                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    levelsList.add(jsonArray.get(i).getAsJsonObject().get("id_niveau").getAsString());
                    levelsIdsList.add(jsonArray.get(i).getAsJsonObject().get("id_niv_spec").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Pair<>(levelsList, levelsIdsList);
        }

        protected void onPostExecute(Pair<List<String>, List<String>> result) {
            if (result != null) {
                List<String> levelsList = result.first;
                levelIdsList = result.second;
                if (levelsList != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, levelsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    levelSpinner.setAdapter(adapter);
                }}
        }
    }

    private class FetchSectionsTask extends AsyncTask<String, Void, Pair<List<String>, List<String>>> {
        @Override
        protected Pair<List<String>, List<String>> doInBackground(String... levelIds) {
            List<String> sectionsList = new ArrayList<>();
            List<String> sectionsIdsList = new ArrayList<>();
            try {
                String levelId = levelIds[0];
                String endpointUrl = "https://num.univ-biskra.dz/psp/pspapi/section?level_specialty=" + levelId + "&semester=2&key=appmob";
                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    sectionsList.add(jsonArray.get(i).getAsJsonObject().get("Abrev_fr").getAsString());
                    sectionsIdsList.add(jsonArray.get(i).getAsJsonObject().get("sectionn_id").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Pair<>(sectionsList, sectionsIdsList);
        }


        protected void onPostExecute(Pair<List<String>, List<String>> result) {
            if (result != null) {
                List<String> sectionsList = result.first;
                sectionIdsList = result.second;

                if (sectionsList != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, sectionsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sectionSpinner.setAdapter(adapter);
                }
            }
        }
    }
    private class FetchGropeTask extends AsyncTask<String, Void, Pair<List<String>, List<String>>> {
        @Override
        protected Pair<List<String>, List<String>> doInBackground(String... sectionIds) {
            List<String> gropeList = new ArrayList<>();
            List<String> gropeIdsList = new ArrayList<>();
            try {
                String sectionId = sectionIds[0];
                String endpointUrl = "https://num.univ-biskra.dz/psp/pspapi/group?section=" + sectionId + "&semester=2&key=appmob";

                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Gson gson = new Gson();
                JsonArray jsonArray = gson.fromJson(response.toString(), JsonArray.class);
                for (int i = 0; i < jsonArray.size(); i++) {
                    gropeList.add(jsonArray.get(i).getAsJsonObject().get("Abrev_fr").getAsString());
                    gropeIdsList.add(jsonArray.get(i).getAsJsonObject().get("groupe_id").getAsString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Pair<>(gropeList, gropeIdsList);
        }

        protected void onPostExecute(Pair<List<String>, List<String>> result) {
            if (result != null) {
                List<String> sectionsList = result.first;
                groupeIdsList = result.second;
                if (sectionsList != null) {
                    // Using a Set to ensure uniqueness of sections
                    Set<String> uniqueGroupes = new HashSet<>(sectionsList);
                    List<String> uniqueGroupesList = new ArrayList<>(uniqueGroupes);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_spinner_item, uniqueGroupesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    groupSpinner.setAdapter(adapter);

                }}
        }
    }

    public List<String> getAllSpinnerTexts(Spinner spinner) {
        List<String> spinnerTexts = new ArrayList<>();
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int count = adapter.getCount();
            for (int i = 0; i < count; i++) {
                spinnerTexts.add(adapter.getItem(i));
            }
        }
        return spinnerTexts;
    }

    List<Pair<String, String>> getCollegesNamesAndId() {
        List<Pair<String, String>> resulte = new ArrayList<>();
        List<String> namesOfColleges = getAllSpinnerTexts(collegeSpinner);

        for (int i = 0; i < namesOfColleges.size(); i++) {
            resulte.add(new Pair<>(namesOfColleges.get(i), "12"));
        }

        return resulte;
    }

}