package com.example.starline.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class SpaceDataRepository {

    val planets: List<Planet> = listOf(
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

    val satellites: List<Satellite> = listOf(
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

    val news: List<NewsArticle> = listOf(
        NewsArticle(
            id = "news_1",
            title = "New Exoplanet Discovered in Habitable Zone",
            category = "Discovery",
            date = "2026-05-15",
            summary = "Astronomers have found a potentially Earth-like planet orbiting a nearby red dwarf star within its habitable zone, indicating the presence of liquid surface water.",
            fullText = "Using advanced spectroscopic analysis from the James Webb Space Telescope and ground-based observatories, international astronomers have discovered 'Gliese 581e', a rocky planet approximately 1.3 times the size of Earth. Located just 20.3 light-years away, this planet orbits closely within its parent red dwarf star's 'Goldilocks' habitable zone, where surface temperatures are mild enough to permit liquid water. Atmospheric readouts suggest a thick nitrogen-oxygen mix with trace carbon dioxide, making it one of the highest-rated targets for future astrobiological exploration.",
            readTime = "3 min read"
        ),
        NewsArticle(
            id = "news_2",
            title = "Black Hole Collision Creates Colossal Gravitational Waves",
            category = "Physics",
            date = "2026-05-12",
            summary = "LIGO and Virgo detectors have registered a massive cosmic ripple generated by the merger of two supermassive black holes billions of light-years away.",
            fullText = "A massive cosmic tremor has shaken the universe, and our detectors have caught it. The Laser Interferometer Gravitational-Wave Observatory (LIGO) in the United States and the Virgo detector in Europe registered gravitational wave event GW260512. The source was a catastrophic collision of two supermassive black holes, weighing 85 and 66 times the mass of the Sun, which occurred approximately 5 billion years ago. The resulting merger converted 8 full solar masses of matter entirely into pure gravitational wave energy in a fraction of a second, warping the fabric of space-time and traveling across the cosmos to reach our sensors.",
            readTime = "4 min read"
        ),
        NewsArticle(
            id = "news_3",
            title = "Artemis Mission Updates: NASA Schedules Crewed Moon Orbit",
            category = "Exploration",
            date = "2026-05-10",
            summary = "NASA shares milestones for the Artemis program, confirming the upcoming lunar flyby and testing the new deep-space Orion crew capsule.",
            fullText = "NASA has successfully completed the critical systems check of the SLS (Space Launch System) booster core and Orion crew module ahead of the historic Artemis crewed lunar orbit mission. Designed to pave the way for a permanent lunar base and eventual human missions to Mars, the upcoming mission will carry four astronauts around the far side of the Moon. Testing advanced life support systems, radiation shields, and navigation loops, the crew will travel further into deep space than any human in history, establishing the foundational logistics for standard lunar surface excursions.",
            readTime = "3 min read"
        ),
        NewsArticle(
            id = "news_4",
            title = "Immense Underground Water Ice Reservoirs Scanned on Mars",
            category = "Discovery",
            date = "2026-05-08",
            summary = "Subsurface radar scans from the Mars Express orbiter reveal extensive water-ice sheets buried beneath the dusty equatorial plains of Elysium Planitia.",
            fullText = "Data gathered by the MARSIS radar sounder aboard ESA's Mars Express orbiter has revealed that the vast dusty plains of Elysium Planitia, located near Mars' equator, hide a massive secret: underground glaciers of water ice extending hundreds of meters deep. Previously, water ice was believed to be locked entirely in Mars' high-latitude polar ice caps. This equatorial discovery suggests Mars was once much wetter than currently imagined, and offers a highly accessible and massive resource of drinking water and rocket fuel for future manned Mars landing expeditions.",
            readTime = "5 min read"
        )
    )

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

    fun getRandomFact(): String {
        val index = Random.nextInt(spaceFacts.size)
        return spaceFacts[index]
    }
}
