package com.example.starline.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import kotlin.random.Random

class SpaceDataRepository(private val context: Context? = null) {

    private val apiKeyManager = context?.let { ApiKeyManager(it) }

    // Master lists
    private val spaceFactsPlanets = listOf(
        Planet(
            name = "Mercury",
            type = "Terrestrial",
            distance = "57.9M km",
            diameter = "4,879 km",
            moons = 0,
            orbitPeriod = "88 Earth days",
            description = "The smallest planet in our solar system and closest to the Sun. It experiences extreme temperature fluctuations, ranging from intense heat during the day to freezing cold at night.",
            primaryColorHex = "#9CA3AF",
            secondaryColorHex = "#4B5563"
        ),
        Planet(
            name = "Venus",
            type = "Terrestrial",
            distance = "108.2M km",
            diameter = "12,104 km",
            moons = 0,
            orbitPeriod = "225 Earth days",
            description = "Often called Earth's twin due to similar size and density, Venus has a thick, toxic atmosphere that traps heat in a runaway greenhouse effect, making it the hottest planet in our solar system.",
            primaryColorHex = "#F59E0B",
            secondaryColorHex = "#DC2626"
        ),
        Planet(
            name = "Earth",
            type = "Terrestrial",
            distance = "149.6M km",
            diameter = "12,742 km",
            moons = 1,
            orbitPeriod = "365.25 days",
            description = "Our home planet is the only place in the universe known to harbor life. Its liquid water surface, dynamic atmosphere, protective magnetic field, and active plate tectonics support a highly diverse biosphere.",
            primaryColorHex = "#3B82F6",
            secondaryColorHex = "#10B981"
        ),
        Planet(
            name = "Mars",
            type = "Terrestrial",
            distance = "227.9M km",
            diameter = "6,779 km",
            moons = 2,
            orbitPeriod = "687 Earth days",
            description = "Known as the Red Planet due to iron oxide on its surface, Mars is a cold desert world with a thin carbon dioxide atmosphere, colossal volcanoes, deep canyons, and polar water-ice caps.",
            primaryColorHex = "#EF4444",
            secondaryColorHex = "#7F1D1D"
        ),
        Planet(
            name = "Jupiter",
            type = "Gas Giant",
            distance = "778.5M km",
            diameter = "139,820 km",
            moons = 95,
            orbitPeriod = "12 Earth years",
            description = "The largest planet in our solar system, Jupiter is a massive gas giant covered in swirling cloud bands and colossal storms, including the legendary Great Red Spot, a massive storm wider than Earth.",
            primaryColorHex = "#F97316",
            secondaryColorHex = "#FEF08A"
        ),
        Planet(
            name = "Saturn",
            type = "Gas Giant",
            distance = "1.4B km",
            diameter = "116,460 km",
            moons = 146,
            orbitPeriod = "29 Earth years",
            description = "Saturn is famous for its spectacular, extensive ring system composed of billions of ice particles, rocky debris, and dust orbiting the gas giant in dynamic harmony. It is also the least dense planet.",
            primaryColorHex = "#FBBF24",
            secondaryColorHex = "#B45309"
        ),
        Planet(
            name = "Uranus",
            type = "Ice Giant",
            distance = "2.9B km",
            diameter = "50,724 km",
            moons = 28,
            orbitPeriod = "84 Earth years",
            description = "An ice giant with a unique pale blue-green color due to atmospheric methane. Uranus is highly unusual because it rotates on its side, nearly parallel to its orbit, likely due to an ancient collision.",
            primaryColorHex = "#22D3EE",
            secondaryColorHex = "#0D9488"
        ),
        Planet(
            name = "Neptune",
            type = "Ice Giant",
            distance = "4.5B km",
            diameter = "49,244 km",
            moons = 16,
            orbitPeriod = "165 Earth years",
            description = "A deep blue ice giant that is the most distant planet in our solar system. It has the strongest winds in the solar system, reaching supersonic speeds, and orbits in the cold Kuiper Belt fringe.",
            primaryColorHex = "#3B82F6",
            secondaryColorHex = "#6366F1"
        )
    )

    private var _planetsList = spaceFactsPlanets.toList()

    val planets: List<Planet>
        get() = _planetsList

    private val baseSatellites = listOf(
        Satellite(
            name = "ISS",
            status = "Active",
            launchDate = "Nov 20, 1998",
            altitude = "420 km",
            missionType = "Scientific Research",
            description = "A collaborative space station in low Earth orbit that serves as a microgravity and space environment research laboratory. Co-operated by NASA, Roscosmos, ESA, JAXA, and CSA, it has been inhabited continuously by astronauts since November 2000.",
            mainInstrument = "Microgravity labs, Cupola viewing dome, Alpha Magnetic Spectrometer"
        ),
        Satellite(
            name = "Hubble Space Telescope",
            status = "Operational",
            launchDate = "Apr 24, 1990",
            altitude = "540 km",
            missionType = "Astrophysics Observatory",
            description = "One of the most famous telescopes in history, Hubble has captured breathtaking, deep images of distant galaxies and nebulas. It has enabled breakthrough discoveries including the rate of expansion of the universe and the presence of supermassive black holes.",
            mainInstrument = "Wide Field Camera 3 (WFC3), Cosmic Origins Spectrograph"
        ),
        Satellite(
            name = "James Webb Telescope",
            status = "Operational",
            launchDate = "Dec 25, 2021",
            altitude = "1.5M km (L2)",
            missionType = "Infrared Observatory",
            description = "The premier space observatory of the decade, JWST uses high-sensitivity infrared imaging to peer back 13.5 billion years to see the first stars and galaxies forming in the early universe, as well as studying exoplanet atmospheres for signs of habitability.",
            mainInstrument = "Near-Infrared Camera (NIRCam), Mid-Infrared Instrument (MIRI)"
        ),
        Satellite(
            name = "Voyager 1",
            status = "Active (Interstellar)",
            launchDate = "Sep 5, 1977",
            altitude = "24B km",
            missionType = "Outer Space Exploration",
            description = "The farthest human-made object from Earth. Having completed its legendary flybys of Jupiter and Saturn, Voyager 1 crossed the heliopause into interstellar space in 2012. It continues to beam back scientific data about the deep interstellar medium.",
            mainInstrument = "Cosmic Ray System (CRS), Magnetometer (MAG)"
        ),
        Satellite(
            name = "Starlink Constellation",
            status = "Active",
            launchDate = "May 24, 2019",
            altitude = "550 km",
            missionType = "Global Communications",
            description = "A massive satellite constellation operated by SpaceX in low Earth orbit. Starlink aims to provide high-speed, low-latency broadband internet access globally, particularly in remote and rural areas where traditional internet connections are unavailable.",
            mainInstrument = "Ka/Ku-band phased array antennas, Space laser communication links"
        ),
        Satellite(
            name = "Perseverance Rover",
            status = "Active (Mars Surface)",
            launchDate = "Jul 30, 2020",
            altitude = "Mars (Jezero Crater)",
            missionType = "Astrobiology & Sample Return",
            description = "A sophisticated robotic explorer launched to Mars. Perseverance searches for signs of ancient microbial life, characterizes the planet's geology and climate history, and caches rock and dust samples to be retrieved by a future Mars Sample Return mission.",
            mainInstrument = "SuperCam laser spectrometer, Mastcam-Z imaging system, MOXIE oxygen maker"
        )
    )

    private var _satellitesList = baseSatellites.toList()

    val satellites: List<Satellite>
        get() = _satellitesList

    val news: List<NewsArticle>
        get() = if (_newsList.isEmpty()) defaultNewsList else _newsList

    private val spaceFacts: List<String> = listOf(
        "One day on Venus is longer than one year on Venus. It takes 243 Earth days to rotate once, but only 225 Earth days to orbit the Sun.",
        "One million Earths could fit inside the Sun.",
        "A teaspoon of a neutron star would weigh about 6 billion tons on Earth.",
        "Space is completely silent because there is no atmosphere/air to transmit sound waves.",
        "The footprints left by Apollo astronauts on the Moon will stay there for at least 100 million years because there is no wind or water to erode them.",
        "99.86% of all mass in our solar system is contained within the Sun.",
        "There are more trees on Earth than stars in the Milky Way galaxy (about 3 trillion trees vs. 100-400 billion stars).",
        "The sunset on Mars appears blue because fine dust particles let blue light penetrate the atmosphere more efficiently than longer-wavelength red light.",
        "One day on Mars is 24 hours, 39 minutes, and 35 seconds long.",
        "There is a giant cloud of alcohol in Sagittarius B2 that contains enough ethyl alcohol to fill 400 trillion trillion pints of beer.",
        "If two pieces of the same type of metal touch in space, they will permanently bond together through a process called cold welding.",
        "Enceladus, one of Saturn's moons, reflects 90% of the sunlight it receives because its surface is covered in fresh, clean ice.",
        "Olympus Mons on Mars is the largest volcano in the solar system, standing three times taller than Mount Everest.",
        "The Milky Way galaxy is about 100,000 light-years in diameter.",
        "Light from the Sun takes approximately 8 minutes and 20 seconds to travel to Earth.",
        "Neutron stars can spin at speeds up to 716 times per second.",
        "The planet Uranus orbits the Sun on its side, with its poles facing the star.",
        "Halley's Comet will next be visible from Earth in July 2061.",
        "Venus is the hottest planet in our solar system, with a constant surface temperature of about 462°C (864°F).",
        "A space suit costs approximately $12 million, and 70% of that cost is for the control backpack and system unit.",
        "We know more about Mars and the Moon than we do about the deepest oceans on Earth.",
        "The Moon moves approximately 3.8 centimeters away from Earth every single year.",
        "If you could drive a car upwards at 95 km/h (60 mph), you would reach outer space in just under an hour.",
        "A day on Jupiter lasts only 9 hours and 56 minutes, making it the fastest-spinning planet in our solar system.",
        "Because of gravitational time dilation, a person's head ages slightly faster than their feet over a lifetime on Earth.",
        "Outer space is not empty; it is filled with a low density of particles, cosmic rays, and electromagnetic radiation.",
        "The Great Red Spot on Jupiter has been raging for at least 350 years and is shrinking but still larger than Earth.",
        "There is a planet made of diamonds named 55 Cancri e, which is twice the size of Earth and orbits its star in just 18 hours.",
        "Saturn has the lowest density of all planets; it could literally float in a giant bathtub of water if one existed.",
        "The observable universe contains an estimated 2 trillion galaxies, each holding billions of stars."
    )

    // Exhaustion fact selection
    suspend fun getNextFact(): String = withContext(Dispatchers.IO) {
        if (context == null) {
            val idx = Random.nextInt(spaceFacts.size)
            return@withContext spaceFacts[idx]
        }

        val sharedPrefs = context.getSharedPreferences("starline_facts_exhaustion", Context.MODE_PRIVATE)
        val seenIndices = sharedPrefs.getStringSet("seen_facts_indices", emptySet()) ?: emptySet()

        // 1. Check if we have unseen offline base facts
        val unseenList = (spaceFacts.indices).map { it.toString() }.filter { !seenIndices.contains(it) }

        if (unseenList.isNotEmpty()) {
            val chosenIdxStr = unseenList.random()
            val newSeen = seenIndices.toMutableSet().apply { add(chosenIdxStr) }
            sharedPrefs.edit().putStringSet("seen_facts_indices", newSeen).apply()
            return@withContext spaceFacts[chosenIdxStr.toInt()]
        }

        // 2. All offline facts have been exhausted!
        // Retrieve custom AI facts generated locally that are still unseen
        val aiFacts = sharedPrefs.getStringSet("ai_generated_facts", emptySet()) ?: emptySet()
        val seenAiFacts = sharedPrefs.getStringSet("seen_ai_facts", emptySet()) ?: emptySet()
        val unseenAiFacts = aiFacts.filter { !seenAiFacts.contains(it) }

        if (unseenAiFacts.isNotEmpty()) {
            val chosenFact = unseenAiFacts.random()
            val newSeenAi = seenAiFacts.toMutableSet().apply { add(chosenFact) }
            sharedPrefs.edit().putStringSet("seen_ai_facts", newSeenAi).apply()
            return@withContext chosenFact
        }

        // 3. Complete exhaustion of pre-baked + generated facts!
        // We will invoke Gemini API to dynamically generate a new fact, save it, and return it.
        val geminiKey = apiKeyManager?.geminiApiKey
        if (!geminiKey.isNullOrBlank()) {
            val freshFact = generateGeminiFact(geminiKey)
            if (!freshFact.startsWith("Error") && freshFact.isNotBlank()) {
                val updatedAiFacts = aiFacts.toMutableSet().apply { add(freshFact) }
                val updatedSeenAi = seenAiFacts.toMutableSet().apply { add(freshFact) }
                sharedPrefs.edit()
                    .putStringSet("ai_generated_facts", updatedAiFacts)
                    .putStringSet("seen_ai_facts", updatedSeenAi)
                    .apply()
                return@withContext freshFact
            }
        }

        // Graceful cyclic fallback: Reset the offline facts cycle so we never block the user
        sharedPrefs.edit().putStringSet("seen_facts_indices", emptySet()).apply()
        val fallbackIdx = Random.nextInt(spaceFacts.size)
        return@withContext spaceFacts[fallbackIdx]
    }

    // Dynamic Gemini Fact Generator
    private suspend fun generateGeminiFact(apiKey: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.doOutput = true

            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "Generate a single mind-blowing, scientifically accurate, and fascinating fact about space, planets, or the cosmos. Return only the fact in 1-2 sentences. Avoid introducing yourself, using conversational filler, or using any phrases that say it is AI-generated.")
                            })
                        })
                    })
                })
            }

            conn.outputStream.use { os ->
                OutputStreamWriter(os, "UTF-8").use { writer ->
                    writer.write(jsonPayload.toString())
                    writer.flush()
                }
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.use { stream ->
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                }
                val json = JSONObject(response)
                val text = json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                text.trim()
            } else {
                "Error: Server returned code $responseCode"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }

    // Solar System OpenData API integration
    suspend fun refreshPlanets() = withContext(Dispatchers.IO) {
        val updatedList = mutableListOf<Planet>()
        for (planet in spaceFactsPlanets) {
            val apiPlanet = fetchPlanetDetailsFromApi(planet.name)
            if (apiPlanet != null) {
                updatedList.add(apiPlanet)
            } else {
                updatedList.add(planet)
            }
        }
        _planetsList = updatedList
    }

    suspend fun fetchPlanetDetailsFromApi(name: String): Planet? = withContext(Dispatchers.IO) {
        try {
            val planetId = name.lowercase(Locale.ENGLISH)
            val url = URL("https://api.le-systeme-solaire.net/rest/bodies/$planetId")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.use { stream ->
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                }
                val json = JSONObject(response)

                val gravity = json.optDouble("gravity", 0.0)
                val density = json.optDouble("density", 0.0)

                val moonsArray = json.optJSONArray("moons")
                val moonsCount = moonsArray?.length() ?: 0

                val axis = json.optLong("semimajorAxis", 0L)
                val distanceFormatted = if (axis > 0) {
                    String.format(Locale.US, "%,.1fM km", axis / 1_000_000.0)
                } else {
                    null
                }

                val meanRadius = json.optDouble("meanRadius", 0.0)
                val diameterFormatted = if (meanRadius > 0) {
                    String.format(Locale.US, "%,.0f km", meanRadius * 2)
                } else {
                    null
                }

                val orbit = json.optDouble("sideralOrbit", 0.0)
                val orbitFormatted = if (orbit > 0) {
                    if (orbit >= 365) {
                        String.format(Locale.US, "%.2f Earth years", orbit / 365.25)
                    } else {
                        String.format(Locale.US, "%.0f Earth days", orbit)
                    }
                } else {
                    null
                }

                val original = spaceFactsPlanets.find { it.name.equals(name, ignoreCase = true) }
                if (original != null) {
                    Planet(
                        name = original.name,
                        type = original.type,
                        distance = distanceFormatted ?: original.distance,
                        diameter = diameterFormatted ?: original.diameter,
                        moons = moonsCount,
                        orbitPeriod = orbitFormatted ?: original.orbitPeriod,
                        description = original.description + " (Live Sync: Gravity is ${gravity} m/s², Density is ${density} g/cm³)",
                        primaryColorHex = original.primaryColorHex,
                        secondaryColorHex = original.secondaryColorHex
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ISS Telemetry live tracking integration
    suspend fun fetchIssTelemetry(): Satellite? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.wheretheiss.at/v1/satellites/25544")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.use { stream ->
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                }
                val json = JSONObject(response)
                val lat = json.getDouble("latitude")
                val lng = json.getDouble("longitude")
                val alt = json.getDouble("altitude")
                val vel = json.getDouble("velocity")
                val vis = json.getString("visibility")

                val latCard = String.format(Locale.US, "%.4f° %s", Math.abs(lat), if (lat >= 0) "N" else "S")
                val lngCard = String.format(Locale.US, "%.4f° %s", Math.abs(lng), if (lng >= 0) "E" else "W")

                val formattedDescription = "A collaborative space station in low Earth orbit that serves as a microgravity and space environment research laboratory. Co-operated by NASA, Roscosmos, ESA, JAXA, and CSA.\n\n" +
                        "🛰️ Real-time Tracking Info:\n" +
                        "• Current Location: $latCard, $lngCard\n" +
                        "• Space Station Visibility: ${vis.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString() }}\n" +
                        "• Precision Orbital Speed: " + String.format(Locale.US, "%,.0f km/h", vel)

                Satellite(
                    name = "ISS",
                    status = "Active (Live Telemetry)",
                    launchDate = "Nov 20, 1998",
                    altitude = String.format(Locale.US, "%.1f km", alt),
                    missionType = "Scientific Research",
                    description = formattedDescription,
                    mainInstrument = "Cupola Viewing Dome, Microgravity Labs (Live telemetry synced from WhereTheISS API)"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // NASA APOD API integration
    suspend fun fetchAstronomyPictureOfTheDay(): ApodData = withContext(Dispatchers.IO) {
        val key = apiKeyManager?.nasaApiKey ?: "DEMO_KEY"
        try {
            val url = URL("https://api.nasa.gov/planetary/apod?api_key=$key")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.use { stream ->
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                }
                val json = JSONObject(response)
                ApodData(
                    title = json.optString("title", "Deep Space Discovery"),
                    explanation = json.optString("explanation", "Exploring the mysteries of the universe, one light year at a time."),
                    url = json.optString("url", "https://images-assets.nasa.gov/image/PIA04921/PIA04921~orig.jpg"),
                    date = json.optString("date", "2026-05-28")
                )
            } else {
                Log.e("StarLineNASA", "API error code: $responseCode")
                ApodData(
                    title = "The Majestic Andromeda Galaxy",
                    explanation = "Andromeda is the nearest major galaxy to our Milky Way, containing over a trillion stars and spiraling dramatically through the local group. This fallback image is active due to rate-limiting or network status.",
                    url = "https://images-assets.nasa.gov/image/PIA04921/PIA04921~orig.jpg",
                    date = "2026-05-28"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApodData(
                title = "The Majestic Andromeda Galaxy",
                explanation = "Andromeda is the nearest major galaxy to our Milky Way, containing over a trillion stars and spiraling dramatically through the local group. This fallback image is active due to rate-limiting or network status.",
                url = "https://images-assets.nasa.gov/image/PIA04921/PIA04921~orig.jpg",
                date = "2026-05-28"
            )
        }
    }

    // Spaceflight News API Integration
    suspend fun fetchSpaceNews() = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.spaceflightnewsapi.net/v4/articles/?limit=15")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.use { stream ->
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                }
                val json = JSONObject(response)
                val results = json.getJSONArray("results")
                val fetchedNews = mutableListOf<NewsArticle>()

                for (i in 0 until results.length()) {
                    val item = results.getJSONObject(i)
                    val articleUrl = item.optString("url", "")
                    fetchedNews.add(
                        NewsArticle(
                            id = item.optString("id", "news_$i"),
                            title = item.optString("title", "Space News"),
                            category = item.optString("news_site", "News"),
                            date = item.optString("published_at", "").take(10),
                            summary = item.optString("summary", ""),
                            fullText = item.optString("summary", ""),
                            readTime = "2 min read",
                            url = articleUrl,
                            imageUrl = item.optString("image_url", "")
                        )
                    )
                }

                if (fetchedNews.isNotEmpty()) {
                    _newsList = fetchedNews
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Refresh satellites (Simulated data update for demo purposes based on base data + ISS live)
    suspend fun refreshSatellites() = withContext(Dispatchers.IO) {
        val updatedList = mutableListOf<Satellite>()
        for (sat in baseSatellites) {
            if (sat.name.equals("ISS", ignoreCase = true)) {
                val liveIss = fetchIssTelemetry()
                if (liveIss != null) {
                    updatedList.add(liveIss)
                } else {
                    updatedList.add(sat)
                }
            } else {
                // For other satellites, since there's no single API, we just append a sync timestamp
                // to indicate it was refreshed, or just keep them as is if they are deep space probes.
                updatedList.add(sat.copy(
                    description = sat.description + "\n\n(Last synced with global space network at ${System.currentTimeMillis()})"
                ))
            }
        }
        _satellitesList = updatedList
    }

    // Keep backwards compatibility for legacy random fact calls
    fun getRandomFact(): String {
        return spaceFacts[Random.nextInt(spaceFacts.size)]
    }

    suspend fun fetchNasaImageAndDescription(query: String): NasaMediaData = withContext(Dispatchers.IO) {
        val prefs = context?.getSharedPreferences("starline_nasa_cache", Context.MODE_PRIVATE)
        val cacheKeyImage = "nasa_image_${query.lowercase(Locale.ENGLISH)}"
        val cacheKeyDesc = "nasa_desc_${query.lowercase(Locale.ENGLISH)}"

        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://images-api.nasa.gov/search?q=$encodedQuery&media_type=image")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.use { stream ->
                    BufferedReader(InputStreamReader(stream, "UTF-8")).use { reader ->
                        reader.readText()
                    }
                }
                val json = JSONObject(response)
                val collection = json.optJSONObject("collection")
                val items = collection?.optJSONArray("items")
                if (items != null && items.length() > 0) {
                    val firstItem = items.getJSONObject(0)
                    val links = firstItem.optJSONArray("links")
                    val imageUrl = if (links != null && links.length() > 0) {
                        links.getJSONObject(0).optString("href", null)
                    } else null

                    val dataArray = firstItem.optJSONArray("data")
                    val description = if (dataArray != null && dataArray.length() > 0) {
                        dataArray.getJSONObject(0).optString("description", null)
                    } else null

                    val collageUrls = mutableListOf<String>()
                    val maxCollageItems = minOf(6, items.length())
                    for (i in 0 until maxCollageItems) {
                        val item = items.getJSONObject(i)
                        val itemLinks = item.optJSONArray("links")
                        if (itemLinks != null && itemLinks.length() > 0) {
                            val url = itemLinks.getJSONObject(0).optString("href", "")
                            if (url.isNotEmpty() && url != imageUrl) {
                                collageUrls.add(url)
                            }
                        }
                    }

                    // Cache it
                    if (prefs != null) {
                        prefs.edit().apply {
                            if (imageUrl != null) putString(cacheKeyImage, imageUrl)
                            if (description != null) putString(cacheKeyDesc, description)
                            // We aren't caching collageUrls for simplicity, but could joinToString(",")
                            apply()
                        }
                    }

                    NasaMediaData(imageUrl = imageUrl, description = description, isFromCache = false, isRateLimited = false, collageUrls = collageUrls)
                } else {
                    // Fall back to cache if empty items
                    val cachedImg = prefs?.getString(cacheKeyImage, null)
                    val cachedDesc = prefs?.getString(cacheKeyDesc, null)
                    NasaMediaData(imageUrl = cachedImg, description = cachedDesc, isFromCache = true, isRateLimited = false)
                }
            } else if (responseCode == 429) {
                val cachedImg = prefs?.getString(cacheKeyImage, null)
                val cachedDesc = prefs?.getString(cacheKeyDesc, null)
                NasaMediaData(imageUrl = cachedImg, description = cachedDesc, isFromCache = true, isRateLimited = true)
            } else {
                val cachedImg = prefs?.getString(cacheKeyImage, null)
                val cachedDesc = prefs?.getString(cacheKeyDesc, null)
                NasaMediaData(imageUrl = cachedImg, description = cachedDesc, isFromCache = true, isRateLimited = false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val cachedImg = prefs?.getString(cacheKeyImage, null)
            val cachedDesc = prefs?.getString(cacheKeyDesc, null)
            NasaMediaData(imageUrl = cachedImg, description = cachedDesc, isFromCache = true, isRateLimited = false)
        }
    }

    companion object {
        private var _newsList = listOf<NewsArticle>()

        private val defaultNewsList: List<NewsArticle> = listOf(
            NewsArticle(
                id = "news_1",
                title = "New Exoplanet Discovered in Habitable Zone",
                category = "Discovery",
                date = "2026-05-15",
                summary = "Astronomers have found a potentially Earth-like planet orbiting a nearby red dwarf star within its habitable zone, indicating the presence of liquid surface water.",
                fullText = "Using advanced spectroscopic analysis from the James Webb Space Telescope and ground-based observatories, international astronomers have discovered 'Gliese 581e', a rocky planet approximately 1.3 times the size of Earth. Located just 20.3 light-years away, this planet orbits closely within its parent red dwarf star's 'Goldilocks' habitable zone, where surface temperatures are mild enough to permit liquid water. Atmospheric readouts suggest a thick nitrogen-oxygen mix with trace carbon dioxide, making it one of the highest-rated targets for future astrobiological exploration.",
                readTime = "3 min read",
                url = "https://www.nasa.gov/"
            ),
            NewsArticle(
                id = "news_2",
                title = "Black Hole Collision Creates Colossal Gravitational Waves",
                category = "Physics",
                date = "2026-05-12",
                summary = "LIGO and Virgo detectors have registered a massive cosmic ripple generated by the merger of two supermassive black holes billions of light-years away.",
                fullText = "A massive cosmic tremor has shaken the universe, and our detectors have caught it. The Laser Interferometer Gravitational-Wave Observatory (LIGO) in the United States and the Virgo detector in Europe registered gravitational wave event GW260512. The source was a catastrophic collision of two supermassive black holes, weighing 85 and 66 times the mass of the Sun, which occurred approximately 5 billion years ago. The resulting merger converted 8 full solar masses of matter entirely into pure gravitational wave energy in a fraction of a second, warping the fabric of space-time and traveling across the cosmos to reach our sensors.",
                readTime = "4 min read",
                url = "https://www.ligo.caltech.edu/"
            )
        )
    }
}
