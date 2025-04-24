package com.twilio.conversations.app.data.model

import org.intellij.lang.annotations.Language

data class DatingProfile(
    val id: String,
    val name: String,
    val age: Int,
    val imageUrl: String,
    val bio: String,
    val location: String,
    val distance: Int,
    val isVerified: Boolean,
    val isOnline: Boolean,
    val statusText: String,
    val language: String
) {
    companion object {
        private val names = listOf(
            // Traditional Indian female names
            "Priya", "Ananya", "Diya", "Zara", "Aisha", "Kavya", "Maya", "Riya", "Saanvi", "Tara",
            "Uma", "Vanya", "Wahida", "Xena", "Yamini", "Zoya", "Aanya", "Bhavya", "Chaya", "Dhara",
            "Eira", "Fatima", "Gauri", "Hema", "Indira", "Jaya", "Kali", "Lakshmi", "Meera", "Nisha",
            "Ojaswini", "Pooja", "Qamra", "Radha", "Sita", "Tara", "Uma", "Valli", "Wahida", "Xena",
            // Modern Indian female names
            "Aditi", "Anjali", "Bhavana", "Chandra", "Devi", "Esha", "Fatima", "Gita", "Hema", "Indu",
            "Jaya", "Kala", "Lakshmi", "Maya", "Nalini", "Ojaswini", "Padmini", "Rama", "Sita", "Tara",
            // Popular Indian female names
            "Aaradhya", "Bhavya", "Chandrika", "Deepika", "Eira", "Fatima", "Gauri", "Hema", "Indira",
            "Jaya", "Kavita", "Lalita", "Madhavi", "Nalini", "Ojaswini", "Padmini", "Rama", "Sita", "Tara",
            // Contemporary Indian female names
            "Aisha", "Bhavana", "Chaya", "Deepa", "Eira", "Fatima", "Gauri", "Hema", "Indira", "Jaya",
            "Kavita", "Lalita", "Madhavi", "Nalini", "Ojaswini", "Padmini", "Rama", "Sita", "Tara", "Uma",
            // Regional Indian female names
            "Aishwarya", "Bhavani", "Chandana", "Deepika", "Eira", "Fatima", "Gauri", "Hema", "Indira",
            "Jaya", "Kavita", "Lalita", "Madhavi", "Nalini", "Ojaswini", "Padmini", "Rama", "Sita", "Tara"
        )

        private val locations = listOf(
            // Major cities
            "Mumbai", "Delhi", "Bangalore", "Hyderabad", "Chennai", "Kolkata", "Ahmedabad", "Pune",
            "Jaipur", "Surat", "Lucknow", "Kanpur", "Nagpur", "Indore", "Thane", "Bhopal", "Visakhapatnam",
            "Patna", "Vadodara", "Ghaziabad", "Ludhiana", "Agra", "Nashik", "Faridabad", "Meerut",
            "Rajkot", "Varanasi", "Srinagar", "Aurangabad", "Dhanbad", "Amritsar", "Allahabad",
            "Ranchi", "Howrah", "Coimbatore", "Jabalpur", "Gwalior", "Vijayawada", "Jodhpur", "Madurai",
            "Raipur", "Kota", "Chandigarh", "Thiruvananthapuram", "Guwahati", "Dehradun", "Mysore",
            "Tiruchirappalli", "Bhubaneswar", "Salem", "Warangal", "Guntur", "Bhiwandi", "Saharanpur",
            "Gorakhpur", "Bikaner", "Amravati", "Noida", "Jamshedpur", "Bhilai", "Cuttack", "Firozabad",
            "Kochi", "Nellore", "Bhavnagar", "Dehradun", "Durgapur", "Asansol", "Rourkela", "Nangloi Jat",
            "Kolhapur", "Ajmer", "Akola", "Gulbarga", "Jamnagar", "Ujjain", "Loni", "Siliguri", "Jhansi",
            "Ulhasnagar", "Jammu", "Sangli-Miraj & Kupwad", "Mangalore", "Erode", "Belgaum", "Ambattur",
            "Tirunelveli", "Malegaon", "Gaya", "Jalgaon", "Udaipur", "Maheshtala", "Davanagere", "Kozhikode",
            "Kurnool", "Rajpur Sonarpur", "Rajahmundry", "Bokaro", "South Dumdum", "Bellary", "Patiala",
            "Gopalpur", "Agartala", "Bhagalpur", "Muzaffarnagar", "Bhatpara", "Panihati", "Latur", "Dhule",
            "Rohtak", "Korba", "Bhilwara", "Brahmapur", "Muzaffarpur", "Ahmednagar", "Mathura", "Kollam",
            "Avadi", "Kadapa", "Kamarhati", "Sambalpur", "Bilaspur", "Shahjahanpur", "Satara", "Bijapur",
            "Rampur", "Shivamogga", "Chandrapur", "Junagadh", "Thrissur", "Alwar", "Bathinda", "Naihati",
            "Baranagar", "Tirupati", "Karnal", "Bharatpur", "Purnia", "Hassan", "Ambala", "Hajipur",
            "Bhubaneswar", "Machilipatnam", "Raebareli", "Alwar", "Rampur", "Jalna", "Muzaffarpur",
            "Jalpaiguri", "Budaun", "Mathura", "Faizabad", "Bellary", "Patiala", "Gopalpur", "Agartala"
        )

        private val professions = listOf(
            "Software Engineer", "Doctor", "Teacher", "Artist", "Chef", "Photographer", "Writer", "Designer",
            "Entrepreneur", "Lawyer", "Architect", "Scientist", "Musician", "Actor", "Journalist", "Pilot",
            "Engineer", "Nurse", "Psychologist", "Dentist", "Veterinarian", "Accountant", "Marketing Manager",
            "Fashion Designer", "Interior Designer", "Personal Trainer", "Yoga Instructor", "Real Estate Agent",
            "Financial Analyst", "Research Scientist", "Product Manager", "Data Scientist", "UX Designer",
            "Graphic Designer", "Social Worker", "Physical Therapist", "Dental Hygienist", "Occupational Therapist",
            "Speech Pathologist", "Pharmacist", "Optometrist", "Chiropractor", "Massage Therapist", "Nutritionist",
            "Life Coach", "Career Counselor", "Event Planner", "Travel Agent", "Tour Guide", "Translator",
            "Librarian", "Curator", "Archaeologist", "Geologist", "Meteorologist", "Astronomer", "Biologist",
            "Chemist", "Physicist", "Mathematician", "Statistician", "Economist", "Political Scientist",
            "Sociologist", "Anthropologist", "Historian", "Philosopher", "Theologian", "Linguist", "Psychiatrist",
            "Counselor", "Therapist", "Coach", "Trainer", "Instructor", "Professor", "Researcher", "Analyst",
            "Consultant", "Advisor", "Manager", "Director", "Executive", "Officer", "Specialist", "Coordinator",
            "Supervisor", "Administrator", "Officer", "Agent", "Representative", "Associate", "Assistant"
        )

        private val interests = listOf(
            "Travel", "Photography", "Music", "Art", "Cooking", "Reading", "Hiking", "Yoga", "Meditation",
            "Fitness", "Dancing", "Gaming", "Movies", "Theater", "Fashion", "Technology", "Science", "Nature",
            "Animals", "Sports", "Coffee", "Wine", "Food", "Culture", "History", "Languages", "Fitness",
            "Wellness", "Adventure", "Photography", "Art", "Music", "Travel", "Cooking", "Reading", "Hiking",
            "Yoga", "Meditation", "Fitness", "Dancing", "Gaming", "Movies", "Theater", "Fashion", "Technology",
            "Science", "Nature", "Animals", "Sports", "Coffee", "Wine", "Food", "Culture", "History", "Languages"
        )

        private val statusMessages = listOf(
            "Available for chat",
            "Looking for meaningful connections",
            "Love exploring new places",
            "Coffee lover ☕",
            "Yoga enthusiast 🧘‍♀️",
            "Adventure seeker 🌟",
            "Foodie at heart 🍜",
            "Music lover 🎵",
            "Bookworm 📚",
            "Travel enthusiast ✈️",
            "Fitness freak 💪",
            "Art lover 🎨",
            "Nature lover 🌿",
            "Tech enthusiast 💻",
            "Dance lover 💃",
            "Movie buff 🎬",
            "Fashionista 👗",
            "Photography lover 📸",
            "Coffee addict ☕",
            "Wine connoisseur 🍷",
            "Food explorer 🍽️",
            "Culture enthusiast 🎭",
            "History buff 📜",
            "Language lover 🗣️",
            "Wellness seeker 🌺",
            "Adventure junkie 🏃‍♀️",
            "Fitness enthusiast 💪",
            "Dance enthusiast 💃",
            "Movie enthusiast 🎬",
            "Fashion lover 👗",
            "Photography enthusiast 📸",
            "Coffee enthusiast ☕",
            "Wine lover 🍷",
            "Food lover 🍽️",
            "Culture lover 🎭",
            "History lover 📜",
            "Language enthusiast 🗣️",
            "Wellness enthusiast 🌺",
            "Adventure lover 🏃‍♀️",
            "Fitness lover 💪",
            "Dance lover 💃",
            "Movie lover 🎬",
            "Fashion enthusiast 👗",
            "Photography lover 📸",
            "Coffee lover ☕",
            "Wine enthusiast 🍷",
            "Food enthusiast 🍽️",
            "Culture buff 🎭",
            "History enthusiast 📜",
            "Language buff 🗣️",
            "Wellness lover 🌺"
        )

        fun getDummyProfiles(): List<DatingProfile> {
            return (1..100).map { index ->
                // Use index to deterministically select values
                val name = names[(index - 1) % names.size]
                val location = locations[(index - 1) % locations.size]
                val profession = professions[(index - 1) % professions.size]
                val interest1 = interests[(index - 1) % interests.size]
                val interest2 = interests[(index + 1) % interests.size]
                val interest3 = interests[(index + 2) % interests.size]
//                val isOnline = index % 3 == 0 // Every third profile is online
                val isOnline = (index % 10) < 8 // 80% of profiles are online
                val isVerified = index % 2 == 0 // Every second profile is verified
                val age = 23 + (index % 23) // Age between 23 and 45
                val distance = 1 + (index % 50) // Distance between 1 and 50
                val imageId = index // Use index for consistent image

                DatingProfile(
                    id = index.toString(),
                    name = name,
                    age = age,
                    imageUrl = "https://picsum.photos/id/$imageId/400/600",
                    bio = "$profession | $interest1 | $interest2",
                    location = location,
                    distance = distance,
                    isVerified = isVerified,
                    isOnline = isOnline,
                    statusText = if (isOnline) "Online" else "Offline",
                    language = if(index % 2 == 0) "Hindi" else "Hindi, English"
                )
            }
        }
    }
} 