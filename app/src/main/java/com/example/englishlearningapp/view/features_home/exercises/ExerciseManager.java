package com.example.englishlearningapp.view.features_home.exercises;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseManager {
    private static final String PREF_NAME = "exercise_preferences";
    private static final String KEY_COMPLETED_EXERCISES = "completed_exercises_";
    private static final String KEY_TOPIC_PROGRESS = "topic_progress_";
    
    private Context context;
    private SharedPreferences sharedPreferences;
    
    public ExerciseManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    // ==================== TOPIC MANAGEMENT ====================
    
    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        
        // Vietnam Topic
        Topic vietnam = new Topic("vietnam", "Việt Nam", 
            "Explore Vietnam's culture, history and landmarks", 
            "vietnam_topic", 7);
        vietnam.setCompletedExercises(getTopicProgress("vietnam"));
        topics.add(vietnam);
        
        // Travel Topic
        Topic travel = new Topic("travel", "Du lịch", 
            "Travel vocabulary and conversations", 
            "travel_topic", 6);
        travel.setCompletedExercises(getTopicProgress("travel"));
        topics.add(travel);
        
        // Electric Vehicle Topic
        Topic ev = new Topic("electric_vehicle", "Xe điện", 
            "Electric vehicles and green technology", 
            "ev_topic", 4);
        ev.setCompletedExercises(getTopicProgress("electric_vehicle"));
        topics.add(ev);
        
        // NASA Topic
        Topic nasa = new Topic("nasa", "NASA", 
            "Space exploration and astronomy", 
            "nasa_topic", 5);
        nasa.setCompletedExercises(getTopicProgress("nasa"));
        topics.add(nasa);
        
        // Animals Topic
        Topic animals = new Topic("animals", "Động vật", 
            "Wildlife and animal conservation", 
            "animals_topic", 5);
        animals.setCompletedExercises(getTopicProgress("animals"));
        topics.add(animals);
        
        // Nature Topic
        Topic nature = new Topic("nature", "Tự nhiên", 
            "Nature, environment and ecology", 
            "nature_topic", 5);
        nature.setCompletedExercises(getTopicProgress("nature"));
        topics.add(nature);
        
        return topics;
    }
    
    public Topic getTopicById(String topicId) {
        List<Topic> topics = getAllTopics();
        for (Topic topic : topics) {
            if (topic.getId().equals(topicId)) {
                return topic;
            }
        }
        return null;
    }
    
    // ==================== EXERCISE DATA ====================
    
    public List<Exercise> getExercisesByTopic(String topicId) {
        switch (topicId) {
            case "vietnam":
                return getVietnamExercises();
            case "travel":
                return getTravelExercises();
            case "electric_vehicle":
                return getElectricVehicleExercises();
            case "nasa":
                return getNasaExercises();
            case "animals":
                return getAnimalsExercises();
            case "nature":
                return getNatureExercises();
            default:
                return new ArrayList<>();
        }
    }
    
    private List<Exercise> getVietnamExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: Ha Long Bay - Easy
        Exercise ex1 = new Exercise("vn_001", "vietnam", "Ha Long Bay Wonder", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex1.setPassage("Ha Long Bay is a UNESCO World Heritage site located in northern Vietnam. " +
            "The bay features thousands of limestone karsts and emerald waters. " +
            "It attracts millions of tourists every year who come to admire its natural beauty.");
        ex1.setPassageWithBlanks("Ha Long Bay is a UNESCO World Heritage ____ located in northern Vietnam. " +
            "The bay features thousands of limestone ____ and emerald waters. " +
            "It attracts millions of ____ every year who come to admire its natural beauty.");
        ex1.setOptions(Arrays.asList("site", "karsts", "tourists", "mountains", "rivers", "visitors"));
        ex1.setCorrectAnswers(Arrays.asList("site", "karsts", "tourists"));
        ex1.setHints(Arrays.asList("A place recognized by UNESCO", "Rock formations in the sea", "People who visit places"));
        ex1.setSource("Vietnam Tourism Board");
        ex1.setCompleted(isExerciseCompleted("vn_001"));
        ex1.setProgress(getExerciseProgress("vn_001"));
        exercises.add(ex1);
        
        // Exercise 2: Vietnamese Cuisine - Medium
        Exercise ex2 = new Exercise("vn_002", "vietnam", "Vietnamese Food Culture", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex2.setPassage("Vietnamese cuisine is known for its fresh ingredients and balanced flavors. " +
            "Pho, the famous noodle soup, originated in northern Vietnam and has become internationally recognized. " +
            "Street food culture plays an important role in Vietnamese daily life, with vendors serving delicious meals at affordable prices.");
        ex2.setPassageWithBlanks("Vietnamese cuisine is known for its fresh ____ and balanced flavors. " +
            "Pho, the famous noodle ____, originated in northern Vietnam and has become internationally ____. " +
            "Street food culture plays an important ____ in Vietnamese daily life, with vendors serving delicious meals at affordable prices.");
        ex2.setOptions(Arrays.asList("ingredients", "soup", "recognized", "role", "spices", "dish", "popular", "part"));
        ex2.setCorrectAnswers(Arrays.asList("ingredients", "soup", "recognized", "role"));
        ex2.setHints(Arrays.asList("What you use to cook", "Liquid food with noodles", "Known worldwide", "Position or function"));
        ex2.setSource("Vietnamese Culinary Institute");
        ex2.setCompleted(isExerciseCompleted("vn_002"));
        ex2.setProgress(getExerciseProgress("vn_002"));
        exercises.add(ex2);
        
        // Exercise 3: Ho Chi Minh City - Hard
        Exercise ex3 = new Exercise("vn_003", "vietnam", "Saigon Economic Hub", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex3.setPassage("Ho Chi Minh City, formerly known as Saigon, is Vietnam's largest metropolitan area and economic powerhouse. " +
            "The city has undergone rapid urbanization and modernization since the 1990s, transforming from a war-torn region into a thriving commercial center. " +
            "Today, it contributes approximately 23% of Vietnam's GDP and houses numerous multinational corporations and startups.");
        ex3.setPassageWithBlanks("Ho Chi Minh City, formerly known as Saigon, is Vietnam's largest ____ area and economic ____. " +
            "The city has undergone rapid ____ and modernization since the 1990s, transforming from a war-torn region into a thriving ____ center. " +
            "Today, it contributes approximately 23% of Vietnam's GDP and houses numerous ____ corporations and startups.");
        ex3.setOptions(Arrays.asList("metropolitan", "powerhouse", "urbanization", "commercial", "multinational", 
            "urban", "financial", "development", "business", "international"));
        ex3.setCorrectAnswers(Arrays.asList("metropolitan", "powerhouse", "urbanization", "commercial", "multinational"));
        ex3.setHints(Arrays.asList("Large city area", "Strong economic force", "City development process", "Business-related", "Operating in many countries"));
        ex3.setSource("Vietnam Economic Times");
        ex3.setCompleted(isExerciseCompleted("vn_003"));
        ex3.setProgress(getExerciseProgress("vn_003"));
        exercises.add(ex3);
        
        // Exercise 4: Mekong Delta - Medium
        Exercise ex4 = new Exercise("vn_004", "vietnam", "Mekong River System", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex4.setPassage("The Mekong Delta is known as the rice bowl of Vietnam, producing over half of the country's rice. " +
            "This fertile region supports millions of farmers and features an intricate network of rivers and canals. " +
            "Floating markets and traditional boat transportation remain important aspects of delta life.");
        ex4.setPassageWithBlanks("The Mekong Delta is known as the rice ____ of Vietnam, producing over half of the country's rice. " +
            "This fertile region supports millions of ____ and features an intricate network of rivers and ____. " +
            "Floating markets and traditional boat ____ remain important aspects of delta life.");
        ex4.setOptions(Arrays.asList("bowl", "farmers", "canals", "transportation", "basket", "workers", "waterways", "movement"));
        ex4.setCorrectAnswers(Arrays.asList("bowl", "farmers", "canals", "transportation"));
        ex4.setHints(Arrays.asList("Container for grain", "People who grow crops", "Man-made waterways", "Moving people and goods"));
        ex4.setSource("Mekong Delta Development");
        ex4.setCompleted(isExerciseCompleted("vn_004"));
        ex4.setProgress(getExerciseProgress("vn_004"));
        exercises.add(ex4);
        
        // Exercise 5: Vietnamese Coffee - Easy
        Exercise ex5 = new Exercise("vn_005", "vietnam", "Coffee Culture", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex5.setPassage("Vietnam is the world's second-largest coffee producer after Brazil. " +
            "Vietnamese coffee is famous for its strong flavor and unique brewing methods. " +
            "Coffee shops are popular social gathering places in both cities and rural areas.");
        ex5.setPassageWithBlanks("Vietnam is the world's second-largest coffee ____ after Brazil. " +
            "Vietnamese coffee is famous for its strong ____ and unique brewing methods. " +
            "Coffee shops are popular social gathering ____ in both cities and rural areas.");
        ex5.setOptions(Arrays.asList("producer", "flavor", "places", "maker", "taste", "spaces"));
        ex5.setCorrectAnswers(Arrays.asList("producer", "flavor", "places"));
        ex5.setHints(Arrays.asList("Something that makes", "How something tastes", "Locations where people meet"));
        ex5.setSource("Vietnam Coffee Association");
        ex5.setCompleted(isExerciseCompleted("vn_005"));
        ex5.setProgress(getExerciseProgress("vn_005"));
        exercises.add(ex5);
        
        // Exercise 6: Hanoi Old Quarter - Medium
        Exercise ex6 = new Exercise("vn_006", "vietnam", "Historic District", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex6.setPassage("Hanoi's Old Quarter preserves centuries of traditional Vietnamese architecture and culture. " +
            "The narrow streets were originally organized by trade guilds, with each street specializing in specific crafts. " +
            "Today, this historic area combines traditional commerce with modern tourism and hospitality.");
        ex6.setPassageWithBlanks("Hanoi's Old Quarter preserves centuries of traditional Vietnamese ____ and culture. " +
            "The narrow streets were originally organized by trade ____, with each street specializing in specific ____. " +
            "Today, this historic area combines traditional ____ with modern tourism and hospitality.");
        ex6.setOptions(Arrays.asList("architecture", "guilds", "crafts", "commerce", "buildings", "groups", "skills", "business"));
        ex6.setCorrectAnswers(Arrays.asList("architecture", "guilds", "crafts", "commerce"));
        ex6.setHints(Arrays.asList("Building design", "Trade organizations", "Skilled work", "Business activities"));
        ex6.setSource("Hanoi Tourism Board");
        ex6.setCompleted(isExerciseCompleted("vn_006"));
        ex6.setProgress(getExerciseProgress("vn_006"));
        exercises.add(ex6);
        
        // Exercise 7: Vietnamese Textiles - Hard
        Exercise ex7 = new Exercise("vn_007", "vietnam", "Traditional Handicrafts", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex7.setPassage("Vietnamese textile production encompasses traditional silk weaving and contemporary fashion manufacturing. " +
            "Ethnic minorities in mountainous regions maintain distinctive weaving techniques passed down through generations. " +
            "The industry has evolved to meet international quality standards while preserving cultural authenticity.");
        ex7.setPassageWithBlanks("Vietnamese textile production encompasses traditional silk ____ and contemporary fashion manufacturing. " +
            "Ethnic minorities in mountainous regions maintain distinctive weaving ____ passed down through generations. " +
            "The industry has evolved to meet international quality ____ while preserving cultural ____.");
        ex7.setOptions(Arrays.asList("weaving", "techniques", "standards", "authenticity", "making", "methods", "requirements", "genuineness"));
        ex7.setCorrectAnswers(Arrays.asList("weaving", "techniques", "standards", "authenticity"));
        ex7.setHints(Arrays.asList("Making fabric", "Special methods", "Level of quality", "Being genuine"));
        ex7.setSource("Vietnam Textile Industry");
        ex7.setCompleted(isExerciseCompleted("vn_007"));
        ex7.setProgress(getExerciseProgress("vn_007"));
        exercises.add(ex7);
        
        // Add more Vietnam exercises...
        return exercises;
    }
    
    private List<Exercise> getTravelExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: Airport Experience - Easy
        Exercise ex1 = new Exercise("tr_001", "travel", "At the Airport", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex1.setPassage("Air travel has become more accessible and convenient for modern travelers. " +
            "Most airports now offer free Wi-Fi, comfortable seating areas, and various dining options. " +
            "Passengers should arrive at least two hours before domestic flights and three hours before international flights.");
        ex1.setPassageWithBlanks("Air travel has become more ____ and convenient for modern travelers. " +
            "Most airports now offer free Wi-Fi, comfortable seating ____, and various dining options. " +
            "Passengers should arrive at least two hours before ____ flights and three hours before international flights.");
        ex1.setOptions(Arrays.asList("accessible", "areas", "domestic", "expensive", "rooms", "local"));
        ex1.setCorrectAnswers(Arrays.asList("accessible", "areas", "domestic"));
        ex1.setHints(Arrays.asList("Easy to reach or use", "Spaces for sitting", "Within the country"));
        ex1.setSource("International Airport Guide");
        ex1.setCompleted(isExerciseCompleted("tr_001"));
        ex1.setProgress(getExerciseProgress("tr_001"));
        exercises.add(ex1);
        
        // Exercise 2: Hotel Booking - Medium
        Exercise ex2 = new Exercise("tr_002", "travel", "Hotel Reservations", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex2.setPassage("Online hotel booking platforms have revolutionized the hospitality industry. " +
            "These platforms allow travelers to compare prices, read reviews, and make instant reservations. " +
            "Many hotels now offer flexible cancellation policies to attract more customers during uncertain times.");
        ex2.setPassageWithBlanks("Online hotel booking ____ have revolutionized the hospitality industry. " +
            "These platforms allow travelers to compare ____, read reviews, and make instant ____. " +
            "Many hotels now offer flexible ____ policies to attract more customers during uncertain times.");
        ex2.setOptions(Arrays.asList("platforms", "prices", "reservations", "cancellation", "websites", "costs", "bookings", "refund"));
        ex2.setCorrectAnswers(Arrays.asList("platforms", "prices", "reservations", "cancellation"));
        ex2.setHints(Arrays.asList("Online systems", "Cost of rooms", "Bookings", "Policy for canceling"));
        ex2.setSource("Travel Industry Report");
        ex2.setCompleted(isExerciseCompleted("tr_002"));
        ex2.setProgress(getExerciseProgress("tr_002"));
        exercises.add(ex2);
        
        // Exercise 3: Cultural Tourism - Hard
        Exercise ex3 = new Exercise("tr_003", "travel", "Sustainable Tourism", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex3.setPassage("Sustainable tourism emphasizes the preservation of natural environments and local cultures. " +
            "Responsible travelers seek to minimize their ecological footprint while maximizing positive economic impact on local communities. " +
            "This approach to tourism promotes long-term viability of destinations and enhances authentic cultural exchanges.");
        ex3.setPassageWithBlanks("Sustainable tourism emphasizes the ____ of natural environments and local cultures. " +
            "Responsible travelers seek to minimize their ecological ____ while maximizing positive economic impact on local ____. " +
            "This approach to tourism promotes long-term ____ of destinations and enhances authentic cultural exchanges.");
        ex3.setOptions(Arrays.asList("preservation", "footprint", "communities", "viability", "protection", "impact", "societies", "sustainability"));
        ex3.setCorrectAnswers(Arrays.asList("preservation", "footprint", "communities", "viability"));
        ex3.setHints(Arrays.asList("Keeping something safe", "Environmental impact", "Local groups of people", "Ability to continue successfully"));
        ex3.setSource("World Tourism Organization");
        ex3.setCompleted(isExerciseCompleted("tr_003"));
        ex3.setProgress(getExerciseProgress("tr_003"));
        exercises.add(ex3);
        
        // Exercise 4: Travel Technology - Medium
        Exercise ex4 = new Exercise("tr_004", "travel", "Digital Travel Tools", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex4.setPassage("Mobile applications have transformed how people plan and experience travel. " +
            "From navigation apps to translation tools, smartphones have become essential travel companions. " +
            "Digital boarding passes and contactless payments have made travel more efficient and hygienic.");
        ex4.setPassageWithBlanks("Mobile ____ have transformed how people plan and experience travel. " +
            "From navigation apps to translation ____, smartphones have become essential travel ____. " +
            "Digital boarding passes and contactless ____ have made travel more efficient and hygienic.");
        ex4.setOptions(Arrays.asList("applications", "tools", "companions", "payments", "programs", "devices", "partners", "transactions"));
        ex4.setCorrectAnswers(Arrays.asList("applications", "tools", "companions", "payments"));
        ex4.setHints(Arrays.asList("Mobile software", "Helpful devices", "Travel partners", "Money transactions"));
        ex4.setSource("Travel Technology Review");
        ex4.setCompleted(isExerciseCompleted("tr_004"));
        ex4.setProgress(getExerciseProgress("tr_004"));
        exercises.add(ex4);
        
        // Exercise 5: Adventure Tourism - Hard
        Exercise ex5 = new Exercise("tr_005", "travel", "Extreme Adventures", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex5.setPassage("Adventure tourism caters to thrill-seekers who desire extraordinary experiences in remote locations. " +
            "Activities such as mountain climbing, deep-sea diving, and wilderness trekking require specialized equipment and expert guidance. " +
            "Safety protocols and emergency preparedness are paramount considerations for adventure tour operators.");
        ex5.setPassageWithBlanks("Adventure tourism caters to thrill-seekers who desire extraordinary ____ in remote locations. " +
            "Activities such as mountain climbing, deep-sea diving, and wilderness trekking require specialized ____ and expert ____. " +
            "Safety protocols and emergency ____ are paramount considerations for adventure tour operators.");
        ex5.setOptions(Arrays.asList("experiences", "equipment", "guidance", "preparedness", "adventures", "gear", "instruction", "readiness"));
        ex5.setCorrectAnswers(Arrays.asList("experiences", "equipment", "guidance", "preparedness"));
        ex5.setHints(Arrays.asList("Things that happen to you", "Special tools needed", "Expert help", "Being ready for emergencies"));
        ex5.setSource("Adventure Travel Association");
        ex5.setCompleted(isExerciseCompleted("tr_005"));
        ex5.setProgress(getExerciseProgress("tr_005"));
        exercises.add(ex5);
        
        // Exercise 6: Business Travel - Medium
        Exercise ex6 = new Exercise("tr_006", "travel", "Corporate Travel", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex6.setPassage("Business travel has evolved significantly with the rise of remote work technologies. " +
            "Companies now prioritize cost-effective solutions while ensuring employee comfort and productivity. " +
            "Virtual meetings have reduced the frequency of business trips, but face-to-face interactions remain valuable for building relationships.");
        ex6.setPassageWithBlanks("Business travel has evolved significantly with the rise of remote work ____. " +
            "Companies now prioritize cost-effective ____ while ensuring employee comfort and ____. " +
            "Virtual meetings have reduced the ____ of business trips, but face-to-face interactions remain valuable for building relationships.");
        ex6.setOptions(Arrays.asList("technologies", "solutions", "productivity", "frequency", "tools", "methods", "efficiency", "occurrence"));
        ex6.setCorrectAnswers(Arrays.asList("technologies", "solutions", "productivity", "frequency"));
        ex6.setHints(Arrays.asList("Technical tools", "Ways to solve problems", "Getting work done", "How often something happens"));
        ex6.setSource("Corporate Travel Management");
        ex6.setCompleted(isExerciseCompleted("tr_006"));
        ex6.setProgress(getExerciseProgress("tr_006"));
        exercises.add(ex6);
        
        return exercises;
    }
    
    private List<Exercise> getElectricVehicleExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: EV Basics - Easy
        Exercise ex1 = new Exercise("ev_001", "electric_vehicle", "Electric Vehicle Fundamentals", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex1.setPassage("Electric vehicles use electricity stored in batteries to power electric motors. " +
            "These vehicles produce zero direct emissions and are much quieter than traditional cars. " +
            "Most electric cars can be charged at home using a standard electrical outlet.");
        ex1.setPassageWithBlanks("Electric vehicles use electricity stored in ____ to power electric motors. " +
            "These vehicles produce zero direct ____ and are much quieter than traditional cars. " +
            "Most electric cars can be charged at home using a standard electrical ____.");
        ex1.setOptions(Arrays.asList("batteries", "emissions", "outlet", "fuel", "pollution", "socket"));
        ex1.setCorrectAnswers(Arrays.asList("batteries", "emissions", "outlet"));
        ex1.setHints(Arrays.asList("Energy storage devices", "Harmful gases released", "Wall connection for power"));
        ex1.setSource("Electric Vehicle Guide");
        ex1.setCompleted(isExerciseCompleted("ev_001"));
        ex1.setProgress(getExerciseProgress("ev_001"));
        exercises.add(ex1);
        
        // Exercise 2: Charging Infrastructure - Medium
        Exercise ex2 = new Exercise("ev_002", "electric_vehicle", "Charging Networks", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex2.setPassage("The expansion of charging infrastructure is crucial for widespread electric vehicle adoption. " +
            "Fast-charging stations can replenish an EV battery to 80% capacity in approximately 30 minutes. " +
            "Governments worldwide are investing billions to establish comprehensive charging networks.");
        ex2.setPassageWithBlanks("The expansion of charging ____ is crucial for widespread electric vehicle adoption. " +
            "Fast-charging stations can replenish an EV battery to 80% ____ in approximately 30 minutes. " +
            "Governments worldwide are investing billions to establish comprehensive charging ____.");
        ex2.setOptions(Arrays.asList("infrastructure", "capacity", "networks", "systems", "level", "grids"));
        ex2.setCorrectAnswers(Arrays.asList("infrastructure", "capacity", "networks"));
        ex2.setHints(Arrays.asList("Basic facilities needed", "Maximum amount it can hold", "Connected systems"));
        ex2.setSource("Charging Infrastructure Report");
        ex2.setCompleted(isExerciseCompleted("ev_002"));
        ex2.setProgress(getExerciseProgress("ev_002"));
        exercises.add(ex2);
        
        // Exercise 3: Battery Technology - Hard
        Exercise ex3 = new Exercise("ev_003", "electric_vehicle", "Advanced Battery Systems", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex3.setPassage("Lithium-ion battery technology has undergone significant improvements in energy density and longevity. " +
            "Next-generation solid-state batteries promise even greater efficiency and safety characteristics. " +
            "Battery recycling and sustainable material sourcing are becoming increasingly important environmental considerations.");
        ex3.setPassageWithBlanks("Lithium-ion battery technology has undergone significant improvements in energy ____ and longevity. " +
            "Next-generation solid-state batteries promise even greater ____ and safety characteristics. " +
            "Battery recycling and sustainable material ____ are becoming increasingly important environmental considerations.");
        ex3.setOptions(Arrays.asList("density", "efficiency", "sourcing", "concentration", "performance", "procurement"));
        ex3.setCorrectAnswers(Arrays.asList("density", "efficiency", "sourcing"));
        ex3.setHints(Arrays.asList("Amount of energy per unit", "How well something works", "Finding and obtaining materials"));
        ex3.setSource("Battery Technology Journal");
        ex3.setCompleted(isExerciseCompleted("ev_003"));
        ex3.setProgress(getExerciseProgress("ev_003"));
        exercises.add(ex3);
        
        // Exercise 4: Environmental Impact - Medium
        Exercise ex4 = new Exercise("ev_004", "electric_vehicle", "Green Transportation", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex4.setPassage("Electric vehicles significantly reduce greenhouse gas emissions compared to conventional vehicles. " +
            "The environmental benefits increase when the electricity comes from renewable energy sources. " +
            "Lifecycle assessments show EVs have a lower carbon footprint despite energy-intensive battery production.");
        ex4.setPassageWithBlanks("Electric vehicles significantly reduce greenhouse gas ____ compared to conventional vehicles. " +
            "The environmental benefits increase when the electricity comes from renewable energy ____. " +
            "Lifecycle assessments show EVs have a lower carbon ____ despite energy-intensive battery production.");
        ex4.setOptions(Arrays.asList("emissions", "sources", "footprint", "releases", "supplies", "impact"));
        ex4.setCorrectAnswers(Arrays.asList("emissions", "sources", "footprint"));
        ex4.setHints(Arrays.asList("Gases released into air", "Places energy comes from", "Environmental impact measure"));
        ex4.setSource("Environmental Protection Agency");
        ex4.setCompleted(isExerciseCompleted("ev_004"));
        ex4.setProgress(getExerciseProgress("ev_004"));
        exercises.add(ex4);
        
        return exercises;
    }
    
    private List<Exercise> getNasaExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: Space Exploration - Easy
        Exercise ex1 = new Exercise("ns_001", "nasa", "Space Mission Basics", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex1.setPassage("NASA is the United States government agency responsible for space exploration and research. " +
            "The agency has sent astronauts to the Moon and operates the International Space Station. " +
            "NASA also studies Earth's climate and weather patterns from space.");
        ex1.setPassageWithBlanks("NASA is the United States government ____ responsible for space exploration and research. " +
            "The agency has sent ____ to the Moon and operates the International Space Station. " +
            "NASA also studies Earth's climate and weather ____ from space.");
        ex1.setOptions(Arrays.asList("agency", "astronauts", "patterns", "organization", "pilots", "systems"));
        ex1.setCorrectAnswers(Arrays.asList("agency", "astronauts", "patterns"));
        ex1.setHints(Arrays.asList("Government organization", "Space travelers", "Regular occurrences"));
        ex1.setSource("NASA Public Information");
        ex1.setCompleted(isExerciseCompleted("ns_001"));
        ex1.setProgress(getExerciseProgress("ns_001"));
        exercises.add(ex1);
        
        // Exercise 2: Mars Mission - Medium
        Exercise ex2 = new Exercise("ns_002", "nasa", "Red Planet Exploration", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex2.setPassage("NASA's Mars exploration program includes robotic rovers that analyze the planet's surface composition. " +
            "These sophisticated machines search for signs of ancient microbial life and study Martian geology. " +
            "Future missions aim to return soil samples to Earth for detailed laboratory analysis.");
        ex2.setPassageWithBlanks("NASA's Mars exploration program includes robotic ____ that analyze the planet's surface composition. " +
            "These sophisticated machines search for signs of ancient microbial ____ and study Martian geology. " +
            "Future missions aim to return soil ____ to Earth for detailed laboratory analysis.");
        ex2.setOptions(Arrays.asList("rovers", "life", "samples", "robots", "organisms", "specimens"));
        ex2.setCorrectAnswers(Arrays.asList("rovers", "life", "samples"));
        ex2.setHints(Arrays.asList("Moving exploration vehicles", "Living organisms", "Small amounts for testing"));
        ex2.setSource("Mars Exploration Program");
        ex2.setCompleted(isExerciseCompleted("ns_002"));
        ex2.setProgress(getExerciseProgress("ns_002"));
        exercises.add(ex2);
        
        // Exercise 3: James Webb Telescope - Hard
        Exercise ex3 = new Exercise("ns_003", "nasa", "Advanced Space Observatory", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex3.setPassage("The James Webb Space Telescope represents a quantum leap in astronomical observation capabilities. " +
            "Its unprecedented infrared sensitivity enables scientists to peer deeper into space and further back in time. " +
            "The telescope's discoveries are revolutionizing our understanding of galaxy formation and exoplanet atmospheres.");
        ex3.setPassageWithBlanks("The James Webb Space Telescope represents a quantum leap in astronomical observation ____. " +
            "Its unprecedented infrared ____ enables scientists to peer deeper into space and further back in time. " +
            "The telescope's discoveries are revolutionizing our understanding of galaxy formation and exoplanet ____.");
        ex3.setOptions(Arrays.asList("capabilities", "sensitivity", "atmospheres", "abilities", "detection", "environments"));
        ex3.setCorrectAnswers(Arrays.asList("capabilities", "sensitivity", "atmospheres"));
        ex3.setHints(Arrays.asList("What something can do", "Ability to detect", "Gas layers around planets"));
        ex3.setSource("James Webb Space Telescope Mission");
        ex3.setCompleted(isExerciseCompleted("ns_003"));
        ex3.setProgress(getExerciseProgress("ns_003"));
        exercises.add(ex3);
        
        // Exercise 4: Artemis Program - Hard
        Exercise ex4 = new Exercise("ns_004", "nasa", "Return to the Moon", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex4.setPassage("The Artemis program aims to establish a sustainable lunar presence for scientific research and exploration. " +
            "This ambitious initiative will land the first woman and next man on the Moon's south pole region. " +
            "The program serves as a stepping stone for eventual human missions to Mars and beyond.");
        ex4.setPassageWithBlanks("The Artemis program aims to establish a sustainable lunar ____ for scientific research and exploration. " +
            "This ambitious initiative will land the first woman and next man on the Moon's south pole ____. " +
            "The program serves as a stepping stone for eventual human missions to Mars and ____.");
        ex4.setOptions(Arrays.asList("presence", "region", "beyond", "existence", "area", "further"));
        ex4.setCorrectAnswers(Arrays.asList("presence", "region", "beyond"));
        ex4.setHints(Arrays.asList("Being there", "Specific area", "Even farther"));
        ex4.setSource("Artemis Mission Overview");
        ex4.setCompleted(isExerciseCompleted("ns_004"));
        ex4.setProgress(getExerciseProgress("ns_004"));
        exercises.add(ex4);
        
        // Exercise 5: Space Station Research - Medium
        Exercise ex5 = new Exercise("ns_005", "nasa", "Orbital Laboratory", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex5.setPassage("The International Space Station serves as a unique microgravity research facility. " +
            "Scientists conduct experiments in biology, physics, and materials science that cannot be performed on Earth. " +
            "The station has been continuously occupied by international crews for over two decades.");
        ex5.setPassageWithBlanks("The International Space Station serves as a unique microgravity research ____. " +
            "Scientists conduct experiments in biology, physics, and materials ____ that cannot be performed on Earth. " +
            "The station has been continuously ____ by international crews for over two decades.");
        ex5.setOptions(Arrays.asList("facility", "science", "occupied", "laboratory", "studies", "inhabited"));
        ex5.setCorrectAnswers(Arrays.asList("facility", "science", "occupied"));
        ex5.setHints(Arrays.asList("A place for research", "Study of materials", "Lived in"));
        ex5.setSource("International Space Station");
        ex5.setCompleted(isExerciseCompleted("ns_005"));
        ex5.setProgress(getExerciseProgress("ns_005"));
        exercises.add(ex5);
        
        return exercises;
    }
    
    private List<Exercise> getAnimalsExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: Wildlife Conservation - Easy
        Exercise ex1 = new Exercise("an_001", "animals", "Protecting Wildlife", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex1.setPassage("Wildlife conservation helps protect endangered species from extinction. " +
            "National parks and reserves provide safe habitats for animals to live and breed. " +
            "Many organizations work together to save threatened species around the world.");
        ex1.setPassageWithBlanks("Wildlife conservation helps protect endangered ____ from extinction. " +
            "National parks and reserves provide safe ____ for animals to live and breed. " +
            "Many organizations work together to save threatened ____ around the world.");
        ex1.setOptions(Arrays.asList("species", "habitats", "animals", "types", "homes", "creatures"));
        ex1.setCorrectAnswers(Arrays.asList("species", "habitats", "species"));
        ex1.setHints(Arrays.asList("Types of animals", "Natural living places", "Types of animals"));
        ex1.setSource("Wildlife Conservation Society");
        ex1.setCompleted(isExerciseCompleted("an_001"));
        ex1.setProgress(getExerciseProgress("an_001"));
        exercises.add(ex1);
        
        // Exercise 2: Ocean Life - Medium
        Exercise ex2 = new Exercise("an_002", "animals", "Marine Ecosystems", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex2.setPassage("Marine ecosystems support an incredible diversity of aquatic life forms. " +
            "Coral reefs serve as underwater cities, providing shelter and food for countless species. " +
            "Ocean pollution and climate change pose serious threats to these delicate environments.");
        ex2.setPassageWithBlanks("Marine ecosystems support an incredible ____ of aquatic life forms. " +
            "Coral reefs serve as underwater ____, providing shelter and food for countless species. " +
            "Ocean pollution and climate change pose serious ____ to these delicate environments.");
        ex2.setOptions(Arrays.asList("diversity", "cities", "threats", "variety", "communities", "dangers"));
        ex2.setCorrectAnswers(Arrays.asList("diversity", "cities", "threats"));
        ex2.setHints(Arrays.asList("Different types", "Large communities", "Dangerous situations"));
        ex2.setSource("Marine Biology Institute");
        ex2.setCompleted(isExerciseCompleted("an_002"));
        ex2.setProgress(getExerciseProgress("an_002"));
        exercises.add(ex2);
        
        // Exercise 3: Primate Intelligence - Hard
        Exercise ex3 = new Exercise("an_003", "animals", "Cognitive Abilities", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex3.setPassage("Primates demonstrate remarkable cognitive abilities including problem-solving and tool use. " +
            "Research has revealed sophisticated social structures and communication systems among great apes. " +
            "These findings challenge traditional assumptions about the boundaries between human and animal intelligence.");
        ex3.setPassageWithBlanks("Primates demonstrate remarkable cognitive ____ including problem-solving and tool use. " +
            "Research has revealed sophisticated social ____ and communication systems among great apes. " +
            "These findings challenge traditional ____ about the boundaries between human and animal intelligence.");
        ex3.setOptions(Arrays.asList("abilities", "structures", "assumptions", "skills", "organizations", "beliefs"));
        ex3.setCorrectAnswers(Arrays.asList("abilities", "structures", "assumptions"));
        ex3.setHints(Arrays.asList("Mental capabilities", "How groups are organized", "Things taken for granted"));
        ex3.setSource("Primate Research Center");
        ex3.setCompleted(isExerciseCompleted("an_003"));
        ex3.setProgress(getExerciseProgress("an_003"));
        exercises.add(ex3);
        
        // Exercise 4: Bird Migration - Medium
        Exercise ex4 = new Exercise("an_004", "animals", "Seasonal Journeys", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex4.setPassage("Bird migration is one of nature's most impressive phenomena, spanning thousands of miles. " +
            "Many species use celestial navigation and magnetic fields to guide their extraordinary journeys. " +
            "Climate change is altering traditional migration patterns, forcing birds to adapt their routes.");
        ex4.setPassageWithBlanks("Bird migration is one of nature's most impressive ____, spanning thousands of miles. " +
            "Many species use celestial navigation and magnetic ____ to guide their extraordinary journeys. " +
            "Climate change is altering traditional migration ____, forcing birds to adapt their routes.");
        ex4.setOptions(Arrays.asList("phenomena", "fields", "patterns", "events", "forces", "routes"));
        ex4.setCorrectAnswers(Arrays.asList("phenomena", "fields", "patterns"));
        ex4.setHints(Arrays.asList("Amazing natural events", "Magnetic areas", "Regular ways of doing things"));
        ex4.setSource("Ornithology Society");
        ex4.setCompleted(isExerciseCompleted("an_004"));
        ex4.setProgress(getExerciseProgress("an_004"));
        exercises.add(ex4);
        
        // Exercise 5: Animal Communication - Hard
        Exercise ex5 = new Exercise("an_005", "animals", "Natural Languages", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex5.setPassage("Animal communication encompasses a vast array of signals including vocalizations, visual displays, and chemical markers. " +
            "Dolphins use sophisticated echolocation systems for navigation and hunting in murky waters. " +
            "Recent studies suggest that some animal communication systems exhibit grammatical complexity previously thought unique to humans.");
        ex5.setPassageWithBlanks("Animal communication encompasses a vast array of signals including vocalizations, visual displays, and chemical ____. " +
            "Dolphins use sophisticated ____ systems for navigation and hunting in murky waters. " +
            "Recent studies suggest that some animal communication systems exhibit grammatical ____ previously thought unique to humans.");
        ex5.setOptions(Arrays.asList("markers", "echolocation", "complexity", "signals", "sonar", "difficulty"));
        ex5.setCorrectAnswers(Arrays.asList("markers", "echolocation", "complexity"));
        ex5.setHints(Arrays.asList("Chemical signs", "Sound-based location system", "Being complicated"));
        ex5.setSource("Animal Behavior Research");
        ex5.setCompleted(isExerciseCompleted("an_005"));
        ex5.setProgress(getExerciseProgress("an_005"));
        exercises.add(ex5);
        
        return exercises;
    }
    
    private List<Exercise> getNatureExercises() {
        List<Exercise> exercises = new ArrayList<>();
        
        // Exercise 1: Forest Ecosystems - Easy
        Exercise ex1 = new Exercise("nt_001", "nature", "Forest Life", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.EASY);
        ex1.setPassage("Forests are home to millions of plant and animal species around the world. " +
            "Trees provide oxygen, clean the air, and help regulate Earth's climate. " +
            "Many forests are being destroyed for agriculture and urban development.");
        ex1.setPassageWithBlanks("Forests are home to millions of plant and animal ____ around the world. " +
            "Trees provide ____, clean the air, and help regulate Earth's climate. " +
            "Many forests are being destroyed for agriculture and urban ____.");
        ex1.setOptions(Arrays.asList("species", "oxygen", "development", "types", "air", "growth"));
        ex1.setCorrectAnswers(Arrays.asList("species", "oxygen", "development"));
        ex1.setHints(Arrays.asList("Different types of life", "Gas we breathe", "Building cities"));
        ex1.setSource("Forest Conservation Report");
        ex1.setCompleted(isExerciseCompleted("nt_001"));
        ex1.setProgress(getExerciseProgress("nt_001"));
        exercises.add(ex1);
        
        // Exercise 2: Climate Change - Medium
        Exercise ex2 = new Exercise("nt_002", "nature", "Global Warming", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex2.setPassage("Climate change is causing rising temperatures and changing weather patterns worldwide. " +
            "Greenhouse gases from human activities trap heat in the atmosphere. " +
            "Scientists warn that immediate action is needed to prevent catastrophic consequences.");
        ex2.setPassageWithBlanks("Climate change is causing rising ____ and changing weather patterns worldwide. " +
            "Greenhouse gases from human activities trap ____ in the atmosphere. " +
            "Scientists warn that immediate action is needed to prevent catastrophic ____.");
        ex2.setOptions(Arrays.asList("temperatures", "heat", "consequences", "degrees", "warmth", "results"));
        ex2.setCorrectAnswers(Arrays.asList("temperatures", "heat", "consequences"));
        ex2.setHints(Arrays.asList("How hot or cold", "Energy that makes things warm", "Results of actions"));
        ex2.setSource("Climate Science Institute");
        ex2.setCompleted(isExerciseCompleted("nt_002"));
        ex2.setProgress(getExerciseProgress("nt_002"));
        exercises.add(ex2);
        
        // Exercise 3: Renewable Energy - Hard
        Exercise ex3 = new Exercise("nt_003", "nature", "Sustainable Power", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex3.setPassage("Renewable energy sources offer sustainable alternatives to fossil fuel dependence. " +
            "Solar panels and wind turbines are becoming increasingly efficient and cost-effective. " +
            "Government incentives and technological innovations are accelerating the transition to clean energy.");
        ex3.setPassageWithBlanks("Renewable energy sources offer sustainable ____ to fossil fuel dependence. " +
            "Solar panels and wind turbines are becoming increasingly ____ and cost-effective. " +
            "Government incentives and technological ____ are accelerating the transition to clean energy.");
        ex3.setOptions(Arrays.asList("alternatives", "efficient", "innovations", "options", "effective", "developments"));
        ex3.setCorrectAnswers(Arrays.asList("alternatives", "efficient", "innovations"));
        ex3.setHints(Arrays.asList("Other choices", "Working well without waste", "New inventions"));
        ex3.setSource("Renewable Energy Council");
        ex3.setCompleted(isExerciseCompleted("nt_003"));
        ex3.setProgress(getExerciseProgress("nt_003"));
        exercises.add(ex3);
        
        // Exercise 4: Ocean Conservation - Medium
        Exercise ex4 = new Exercise("nt_004", "nature", "Protecting Seas", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.MEDIUM);
        ex4.setPassage("Ocean conservation efforts focus on reducing plastic pollution and overfishing. " +
            "Marine protected areas help preserve critical habitats for endangered species. " +
            "International cooperation is essential for addressing global ocean health challenges.");
        ex4.setPassageWithBlanks("Ocean conservation efforts focus on reducing plastic ____ and overfishing. " +
            "Marine protected areas help preserve critical ____ for endangered species. " +
            "International cooperation is essential for addressing global ocean health ____.");
        ex4.setOptions(Arrays.asList("pollution", "habitats", "challenges", "contamination", "homes", "problems"));
        ex4.setCorrectAnswers(Arrays.asList("pollution", "habitats", "challenges"));
        ex4.setHints(Arrays.asList("Making dirty", "Places animals live", "Difficult problems"));
        ex4.setSource("Marine Conservation Alliance");
        ex4.setCompleted(isExerciseCompleted("nt_004"));
        ex4.setProgress(getExerciseProgress("nt_004"));
        exercises.add(ex4);
        
        // Exercise 5: Biodiversity Crisis - Hard
        Exercise ex5 = new Exercise("nt_005", "nature", "Species Extinction", 
            Exercise.ExerciseType.FILL_IN_BLANKS, Exercise.Difficulty.HARD);
        ex5.setPassage("The current biodiversity crisis represents the sixth major extinction event in Earth's history. " +
            "Human activities including habitat destruction and pollution are accelerating species loss rates. " +
            "Conservation biologists emphasize the urgent need for comprehensive ecosystem restoration programs.");
        ex5.setPassageWithBlanks("The current biodiversity crisis represents the sixth major ____ event in Earth's history. " +
            "Human activities including habitat ____ and pollution are accelerating species loss rates. " +
            "Conservation biologists emphasize the urgent need for comprehensive ecosystem ____ programs.");
        ex5.setOptions(Arrays.asList("extinction", "destruction", "restoration", "elimination", "damage", "recovery"));
        ex5.setCorrectAnswers(Arrays.asList("extinction", "destruction", "restoration"));
        ex5.setHints(Arrays.asList("Complete disappearance", "Complete damage", "Bringing back to original state"));
        ex5.setSource("Biodiversity Research Center");
        ex5.setCompleted(isExerciseCompleted("nt_005"));
        ex5.setProgress(getExerciseProgress("nt_005"));
        exercises.add(ex5);
        
        return exercises;
    }
    
    // ==================== PROGRESS MANAGEMENT ====================
    
    public void markExerciseCompleted(String exerciseId, int score) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_COMPLETED_EXERCISES + exerciseId, true);
        editor.putInt("score_" + exerciseId, score);
        editor.apply();
        
        // Update topic progress
        updateTopicProgress(exerciseId);
    }
    
    public boolean isExerciseCompleted(String exerciseId) {
        // Try new format first (saved by our FillBlanksActivity)
        android.content.SharedPreferences exercisePrefs = context.getSharedPreferences("exercise_prefs", Context.MODE_PRIVATE);
        boolean newFormat = exercisePrefs.getBoolean("exercise_" + exerciseId + "_completed", false);
        if (newFormat) return true;
        
        // Fall back to old format
        return sharedPreferences.getBoolean(KEY_COMPLETED_EXERCISES + exerciseId, false);
    }
    
    public float getExerciseProgress(String exerciseId) {
        // Try new format first (saved by our FillBlanksActivity)
        android.content.SharedPreferences exercisePrefs = context.getSharedPreferences("exercise_prefs", Context.MODE_PRIVATE);
        return exercisePrefs.getFloat("exercise_" + exerciseId + "_progress", 0f);
    }
    
    public int getExerciseScore(String exerciseId) {
        return sharedPreferences.getInt("score_" + exerciseId, 0);
    }
    
    private void updateTopicProgress(String exerciseId) {
        String topicId = exerciseId.substring(0, exerciseId.indexOf("_"));
        List<Exercise> exercises = getExercisesByTopic(topicId);
        
        int completed = 0;
        for (Exercise exercise : exercises) {
            if (isExerciseCompleted(exercise.getId())) {
                completed++;
            }
        }
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_TOPIC_PROGRESS + topicId, completed);
        editor.apply();
    }
    
    public int getTopicProgress(String topicId) {
        return sharedPreferences.getInt(KEY_TOPIC_PROGRESS + topicId, 0);
    }
    
    // ==================== STATISTICS ====================
    
    public int getTotalCompletedExercises() {
        List<Topic> topics = getAllTopics();
        int total = 0;
        for (Topic topic : topics) {
            total += topic.getCompletedExercises();
        }
        return total;
    }
    
    public int getTotalExercises() {
        List<Topic> topics = getAllTopics();
        int total = 0;
        for (Topic topic : topics) {
            total += topic.getTotalExercises();
        }
        return total;
    }
    
    public double getOverallProgress() {
        int total = getTotalExercises();
        if (total == 0) return 0.0;
        return (getTotalCompletedExercises() * 100.0) / total;
    }
}
