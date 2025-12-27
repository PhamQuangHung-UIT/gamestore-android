package com.uit.gamestore.data.local;

import com.uit.gamestore.domain.model.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockGameAPI {
    private List<Game>  m_listGames = new ArrayList<>();

    private static MockGameAPI m_instance;

    public static MockGameAPI getInstance() {
        if (m_instance == null) {
            m_instance = new MockGameAPI();
        }
        return m_instance;
    }

    private MockGameAPI() {
         m_listGames.addAll(Arrays.asList(
                new Game(
                        "game_001",
                        "Skyward Saga",
                        "Soar through floating islands in this colorful platformer.",
                        "Skyward Saga is a vibrant platform adventure where you pilot a glider and solve aerial puzzles. Collect upgrades and rescue lost islands.",
                        new Game.Price("USD", 4.99),
                        "https://placehold.co/100x100/yellow/white",
                        "https://placehold.co/320x180/orange/white",
                        Arrays.asList(
                                "https://placehold.co/320x180/orange/white",
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-3"
                ),

                new Game(
                        "game_002",
                        "Pixel Pioneers",
                        "A retro-style sandbox with crafting and exploration.",
                        "Pixel Pioneers blends classic pixel art with modern sandbox mechanics. Build, explore, and survive across procedurally generated worlds.",
                        new Game.Price("USD", 0),
                        "https://placehold.co/100x100/blue/white",
                        "https://placehold.co/600x400/green/white",
                        Arrays.asList(
                                "https://placehold.co/600x400/green/white",
                                "https://placehold.co/600x400/green/white",
                                "https://placehold.co/600x400/green/white"
                        ),
                        "iarc-7"
                ),

                new Game(
                        "game_003",
                        "Neon Drift",
                        "Fast-paced arcade racing with synthwave aesthetics.",
                        "Neon Drift puts you behind the wheel of futuristic vehicles on neon-lit tracks. Drift, boost, and outrun rivals in single and multiplayer modes.",
                        new Game.Price("USD", 0),
                        "https://example.com/icons/neon_drift.png",
                        "https://example.com/banners/neon_drift_banner.png",
                        Arrays.asList(
                                "https://example.com/gallery/neon_1.png",
                                "https://example.com/gallery/neon_2.png"
                        ),
                        "iarc-12"
                ),

                new Game(
                        "game_004",
                        "Mystic Manor",
                        "A narrative-driven mystery in an old manor.",
                        "Explore rooms, uncover secrets, and piece together the story of Mystic Manor. Atmospheric puzzles and branching choices await.",
                        new Game.Price("USD", 6.49),
                        "https://example.com/icons/mystic_manor.png",
                        "https://example.com/banners/mystic_manor_banner.png",
                        Arrays.asList(
                                "https://example.com/gallery/mystic_1.png",
                                "https://example.com/gallery/mystic_2.png",
                                "https://example.com/gallery/mystic_3.png"
                        ),
                        "iarc-12"
                ),

                new Game(
                        "game_005",
                        "Galaxy Traders",
                        "Space trading and combat with deep progression.",
                        "Trade commodities, outfit your ship, and engage in tactical dogfights in an open galaxy. Make allies or become a feared pirate.",
                        new Game.Price("USD", 14.99),
                        "https://example.com/icons/galaxy_traders.png",
                        "https://example.com/banners/galaxy_traders_banner.png",
                        Arrays.asList(
                                "https://example.com/gallery/galaxy_1.png",
                                "https://example.com/gallery/galaxy_2.png",
                                "https://example.com/gallery/galaxy_3.png"
                        ),
                        "iarc-16"
                ),

                new Game(
                        "game_006",
                        "Chef's Quest",
                        "Casual cooking time-management fun for all ages.",
                        "Manage a bustling kitchen, learn recipes, and serve customers quickly to grow your restaurant empire.",
                        new Game.Price("USD", 0.99),
                        "https://placehold.co/100x100/red/white",
                        "https://placehold.co/600x400/gray/white",
                        Arrays.asList(
                                "https://placehold.co/600x400/gray/white",
                                "https://placehold.co/600x400/gray/white",
                                "https://placehold.co/600x400/gray/white"
                        ),
                        "iarc-3"
                ),

                new Game(
                        "game_007",
                        "Shadow Tactics",
                        "Stealth tactics with precision and planning.",
                        "Control a small team of specialists to infiltrate enemy compounds. Emphasize timing, positioning, and silent takedowns.",
                        new Game.Price("USD", 0),
                        "https://placehold.co/100x100/blue/white",
                        "https://placehold.co/600x400/222222/white",
                        Arrays.asList(
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-16"
                ),

                new Game(
                        "game_008",
                        "Farmstead Friends",
                        "Relaxing farming sim with animal companions.",
                        "Plant crops, befriend animals, and decorate your farm. Relaxing progression and daily goals make it a cozy experience.",
                        new Game.Price("USD", 3.49),
                        "https://placehold.co/100x100/orange/white",
                        "https://placehold.co/600x400/yellow/white",
                        Arrays.asList(
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-3"
                ),

                new Game(
                        "game_009",
                        "Rogue Depths",
                        "Challenging roguelike with procedural dungeons.",
                        "Descend into ever-changing depths, collect powerful relics, and master unique character builds each run.",
                        new Game.Price("USD", 7.99),
                        "https://placehold.co/100x100/red/yellow",
                        "https://placehold.co/600x400/orange/yellow",
                        Arrays.asList(
                                "https://placehold.co/600x400/purple/white",
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-12"
                ),

                new Game(
                        "game_010",
                        "Tower Architect",
                        "Physics-based tower building puzzles.",
                        "Stack, balance, and design towers that withstand environmental challenges. Creative tools and community levels included.",
                        new Game.Price("USD", 1.99),
                        "https://placehold.co/100x100/orange/white",
                        "https://placehold.co/600x400/cyan/white",
                        Arrays.asList(
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-7"
                ),

                new Game(
                        "game_011",
                        "Arcane Arena",
                        "Fast MOBA-lite with short matches.",
                        "Pick heroes, use skill combos, and compete in short-session arenas—perfect for quick competitive play.",
                        new Game.Price("USD", 0.00),
                        "https://placehold.co/100x100/pink/white",
                        "https://placehold.co/600x400/363636/white",
                        Arrays.asList(
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-12"
                )
        ));
    }

    public List<Game> getAllGames() {
        return m_listGames;
    }

    public List<Game> getGamesByCategory(String category) {
        return Arrays.asList(
                new Game(
                        "game_014",
                        "Clash of Clans",
                        "Challenging roguelike with procedural dungeons.",
                        "Descend into ever-changing depths, collect powerful relics, and master unique character builds each run.",
                        new Game.Price("USD", 7.99),
                        "https://image.winudf.com/v2/image1/Y29tLnN1cGVyY2VsbC5jbGFzaG9mY2xhbnNfaWNvbl8xNzU5MzIzNDA5XzAwNw/icon.webp?w=280&fakeurl=1&type=.webp",
                        "https://placehold.co/320x180/brown/white/png",
                        Arrays.asList(
                                "https://image.winudf.com/v2/user/admin/YWRtaW5fZmM0N2M5NjFjYmNiNzAyZGMwYWEzNDE1MTlhMDQ2MDlfNzIwLmpwZ18xNzQzNTc4MjU1NzQ4/screen-1.webp?fakeurl=1&type=.webp",
                                "https://image.winudf.com/v2/user/admin/YWRtaW5fZmM0N2M5NjFjYmNiNzAyZGMwYWEzNDE1MTlhMDQ2MDlfNzIwLmpwZ18xNzQzNTc4MjU1NzQ4/screen-1.webp?fakeurl=1&type=.webp"
                        ),
                        "iarc-12"
                ),

                new Game(
                        "game_015",
                        "Tower Architect",
                        "Physics-based tower building puzzles.",
                        "Stack, balance, and design towers that withstand environmental challenges. Creative tools and community levels included.",
                        new Game.Price("USD", 1.99),
                        "https://placehold.co/100x100/orange/white/png",
                        "https://placehold.co/320x180/cyan/white/png",
                        Arrays.asList(
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-7"
                ),

                new Game(
                        "game_016",
                        "Arcane Arena 2: New Hope",
                        "Fast MOBA-lite with short matches.",
                        "Pick heroes, use skill combos, and compete in short-session arenas—perfect for quick competitive play.",
                        new Game.Price("USD", 0.00),
                        "https://placehold.co/100x100/pink/white/png",
                        "https://placehold.co/320x180/363636/white/png",
                        Arrays.asList(
                                "https://placehold.co/600x400/orange/white",
                                "https://placehold.co/600x400/orange/white"
                        ),
                        "iarc-12"
                )
        );
    }
}
