package com.acShop;

import com.acShop.mapper.ProductMapper;
import com.acShop.mapper.ShopMapper;
import com.acShop.mapper.UserMapper;
import com.acShop.pojo.Product;
import com.acShop.pojo.Shop;
import com.acShop.pojo.User;
import com.acShop.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDateTime;

@Component
@Profile("seed")
public class DataSeeder implements ApplicationRunner {

    @Autowired private UserMapper userMapper;
    @Autowired private ShopMapper shopMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private S3Service s3Service;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // [shopName, shopDescription, owner username], then 15 products per shop as [name, description, price]
    private static final Object[][] SHOPS = {
        new Object[]{"Tech World", "Latest gadgets and electronics", "owner01", new String[][]{
            {"Wireless Earbuds",     "Active noise cancellation, 30hr battery",   "149.99"},
            {"Mechanical Keyboard",  "TKL RGB backlit, tactile switches",          "129.99"},
            {"USB-C Hub",            "7-in-1 multiport adapter",                   "49.99"},
            {"4K Webcam",            "Ultra HD webcam with auto focus",            "99.99"},
            {"Phone Stand",          "Adjustable aluminium desk stand",            "24.99"},
            {"Portable SSD",         "1TB NVMe USB-C external drive",             "119.99"},
            {"Smart Plug",           "Wi-Fi enabled power outlet, voice control",  "29.99"},
            {"LED Desk Lamp",        "Touch dimmer, USB charging port",            "39.99"},
            {"Gaming Mouse",         "16000 DPI, programmable buttons",            "79.99"},
            {"Laptop Stand",         "Ergonomic riser, foldable aluminium",        "44.99"},
            {"Screen Cleaner Kit",   "Microfibre cloth and spray solution",         "9.99"},
            {"Cable Organiser",      "Silicone ties and desk clips set",            "12.99"},
            {"Wireless Charger",     "15W fast charge Qi pad",                     "34.99"},
            {"Bluetooth Speaker",    "360° sound, IPX7 waterproof",                "89.99"},
            {"Action Camera",        "4K60fps, waterproof to 40m",                "199.99"},
        }},
        new Object[]{"Fashion Hub", "Trendy clothing and accessories", "owner02", new String[][]{
            {"Classic White Tee",    "100% organic cotton, unisex fit",            "29.99"},
            {"Slim Fit Jeans",       "Stretch denim, tapered leg",                 "79.99"},
            {"Pullover Hoodie",      "Brushed fleece lining, kangaroo pocket",     "59.99"},
            {"Canvas Sneakers",      "Rubber sole, available in 8 colours",        "69.99"},
            {"Baseball Cap",         "Embroidered logo, adjustable strap",         "24.99"},
            {"Linen Shirt",          "Breathable summer shirt, relaxed cut",       "49.99"},
            {"Leather Belt",         "Full-grain leather, silver buckle",          "34.99"},
            {"Crew Socks 3-Pack",    "Cushioned sole, ribbed cuff",                "14.99"},
            {"Chino Trousers",       "Wrinkle-resistant, straight cut",            "64.99"},
            {"Puffer Jacket",        "Lightweight, packable, water-resistant",     "119.99"},
            {"Tote Bag",             "Waxed canvas, interior zip pocket",          "44.99"},
            {"Knit Beanie",          "Merino wool blend, ribbed texture",          "19.99"},
            {"Running Shorts",       "Quick-dry fabric, zip pocket",               "34.99"},
            {"Graphic Sweatshirt",   "Screen-printed artwork, oversized",          "54.99"},
            {"Sunglasses",           "Polarised lenses, UV400 protection",         "49.99"},
        }},
        new Object[]{"Fresh Bites", "Organic food and specialty snacks", "owner03", new String[][]{
            {"Granola Bar 6-Pack",   "Honey oat, no artificial colours",            "8.99"},
            {"Organic Green Tea",    "Loose leaf, 100g resealable pouch",          "12.99"},
            {"Trail Mix 500g",       "Almonds, cashews, cranberries",               "9.99"},
            {"Whey Protein",         "Vanilla, 1kg, 25g protein per serve",        "49.99"},
            {"Dark Chocolate 70%",   "Single-origin cacao, 100g bar",               "6.99"},
            {"Cold Brew Coffee",     "Nitro-infused, 330ml can",                    "4.99"},
            {"Chia Seeds 400g",      "Raw organic, high in omega-3",                "7.99"},
            {"Matcha Powder",        "Ceremonial grade, 50g tin",                  "22.99"},
            {"Coconut Oil 500ml",    "Extra virgin, cold pressed",                 "14.99"},
            {"Protein Balls 12-Pack","No-bake, peanut butter and choc",            "15.99"},
            {"Dried Mango Slices",   "Unsweetened, 200g resealable bag",            "6.99"},
            {"Almond Butter 250g",   "No added sugar, smooth",                     "10.99"},
            {"Rice Cakes 10-Pack",   "Lightly salted, gluten free",                 "3.99"},
            {"Kombucha 330ml",       "Live cultures, ginger lemon flavour",         "4.99"},
            {"Oat Milk 1L",          "Barista edition, froths perfectly",           "3.49"},
        }},
        new Object[]{"Sports Zone", "Equipment for every sport", "owner04", new String[][]{
            {"Yoga Mat",             "6mm thick, non-slip, carry strap included",  "39.99"},
            {"Resistance Bands Set", "5 levels, latex-free",                       "24.99"},
            {"Jump Rope",            "Ball-bearing handles, adjustable cable",     "19.99"},
            {"Foam Roller",          "High-density EVA, 45cm",                     "29.99"},
            {"Water Bottle 1L",      "BPA-free Tritan, leak-proof lid",            "22.99"},
            {"Gym Gloves",           "Padded palm, wrist wrap support",            "18.99"},
            {"Running Belt",         "Waist pack, touchscreen phone window",       "14.99"},
            {"Dumbbell 5kg Pair",    "Neoprene coated, hex shape",                 "34.99"},
            {"Kettlebell 8kg",       "Cast iron, powder coat finish",              "44.99"},
            {"Pull-Up Bar",          "Door frame mount, no screws required",       "29.99"},
            {"Swim Goggles",         "Anti-fog, UV protection, wide lens",         "19.99"},
            {"Compression Socks",    "Graduated, 20-30 mmHg, 2-pack",             "24.99"},
            {"Sports Towel",         "Microfibre, quick-dry, 80x40cm",             "12.99"},
            {"Weightlifting Belt",   "Genuine leather, 4-inch wide",              "59.99"},
            {"Agility Ladder",       "12 rungs, 6m, carry bag included",           "21.99"},
        }},
        new Object[]{"Home Haven", "Furniture and home décor", "owner05", new String[][]{
            {"Scented Candle",       "Soy wax, 40hr burn, cedar & sage",           "18.99"},
            {"Throw Pillow Cover",   "Linen blend, 45x45cm, 2-pack",              "16.99"},
            {"Wall Clock",           "Silent sweep, walnut wood frame",            "39.99"},
            {"Bamboo Tray",          "Handles, multi-purpose, 40x30cm",           "22.99"},
            {"Ceramic Mug Set",      "Handmade, 350ml, set of 4",                 "34.99"},
            {"Fairy Lights 5m",      "Warm white, USB powered, 50 LEDs",          "12.99"},
            {"Linen Napkins 4-Pack", "Hemstitched edge, machine washable",         "19.99"},
            {"Woven Basket",         "Seagrass, round, 30cm diameter",            "27.99"},
            {"Vase Set of 3",        "Clear glass, varying heights",              "24.99"},
            {"Kitchen Timer",        "Mechanical, no batteries, classic style",    "9.99"},
            {"Wooden Cutting Board", "Acacia wood, juice groove, handle",         "34.99"},
            {"Doormat",              "Coir natural fibre, 60x40cm",               "17.99"},
            {"Picture Frame Set",    "White wood, 3-piece: 4x6, 5x7, 8x10",      "28.99"},
            {"Shower Curtain",       "PEVA liner-free, 180x200cm",                "21.99"},
            {"Table Runner",         "Macramé cotton, 180x30cm",                  "23.99"},
        }},
        new Object[]{"Book Nook", "Books for every reader", "owner06", new String[][]{
            {"The Pragmatic Programmer", "20th anniversary edition",              "49.99"},
            {"Clean Code",           "A handbook of agile software",              "44.99"},
            {"Atomic Habits",        "Tiny changes, remarkable results",          "24.99"},
            {"Sapiens",              "A brief history of humankind",              "19.99"},
            {"The Lean Startup",     "Build, measure, learn",                     "22.99"},
            {"Deep Work",            "Rules for focused success",                 "21.99"},
            {"Thinking, Fast & Slow","Two systems of thought",                    "18.99"},
            {"Zero to One",          "Notes on startups and the future",          "20.99"},
            {"The Innovators",       "How a group of hackers created the digital revolution", "23.99"},
            {"Designing Data-Intensive Applications", "Principles of distributed systems", "59.99"},
            {"The Phoenix Project",  "A novel about DevOps",                      "27.99"},
            {"Steve Jobs",           "Walter Isaacson biography",                 "22.99"},
            {"Bad Blood",            "Secrets and lies in a Silicon Valley startup", "19.99"},
            {"No Rules Rules",       "Netflix culture of freedom",                "24.99"},
            {"The Hard Thing About Hard Things", "Building a business when there are no easy answers", "21.99"},
        }},
        new Object[]{"Beauty Bar", "Skincare and cosmetics", "owner07", new String[][]{
            {"Vitamin C Serum 30ml", "15% L-ascorbic acid, brightening",          "34.99"},
            {"Hyaluronic Moisturiser","Fragrance-free, 50ml, all skin types",     "28.99"},
            {"SPF 50 Sunscreen",     "Lightweight, invisible finish, 75ml",       "24.99"},
            {"Micellar Water 400ml", "3-in-1 cleanser, no rinse needed",          "12.99"},
            {"Retinol Night Cream",  "0.3% retinol, anti-ageing, 50ml",          "42.99"},
            {"Rose Water Toner",     "Alcohol-free, hydrating, 200ml",            "14.99"},
            {"Sheet Mask 5-Pack",    "Niacinamide and centella, soothing",        "16.99"},
            {"Lip Balm SPF 15",      "Shea butter, tinted rose, 4.8g",            "6.99"},
            {"Eye Cream 15ml",       "Caffeine complex, depuffing",               "29.99"},
            {"Jade Roller",          "Dual-ended, anti-puff massage",             "19.99"},
            {"Konjac Sponge",        "Gentle daily exfoliant, biodegradable",      "8.99"},
            {"AHA BHA Exfoliant",    "Chemical exfoliant pads, 60 count",         "38.99"},
            {"Cleansing Balm 100ml", "Oil-to-milk formula, removes SPF",          "26.99"},
            {"Niacinamide 10% Serum","Zinc 1%, pore minimising, 30ml",           "22.99"},
            {"Facial Oil 30ml",      "Rosehip seed, squalane blend",              "31.99"},
        }},
        new Object[]{"Toy Chest", "Fun toys for all ages", "owner08", new String[][]{
            {"LEGO Classic Set",     "500 pieces, free-build, ages 4+",           "39.99"},
            {"Wooden Train Set",     "30 pieces, compatible with major brands",   "34.99"},
            {"Remote Control Car",   "1:16 scale, 2.4GHz, 25km/h",               "49.99"},
            {"Magnetic Tiles 60pc",  "STEM building, translucent colours",        "54.99"},
            {"Playdough 8-Pack",     "Non-toxic, bright colours, tools included", "14.99"},
            {"Puzzle 500 Piece",     "Landscape photography, 49x36cm",           "18.99"},
            {"Stuffed Animal Bear",  "Super soft, 35cm, machine washable",        "22.99"},
            {"Water Blaster",        "Pump-action, 1L tank, 8m range",            "16.99"},
            {"Board Game: Catan",    "Strategy classic, 3-4 players",             "54.99"},
            {"Card Game: Uno",       "112 cards, 2-10 players, family fun",        "9.99"},
            {"Science Kit",          "20 experiments, crystals and volcanoes",    "29.99"},
            {"Art Set 120 Pieces",   "Colouring pencils, markers, pastels",       "24.99"},
            {"Jump Stilts",          "Adjustable, up to 50kg, ages 5+",           "27.99"},
            {"Bubble Machine",       "Automatic, 500 bubbles/min, outdoor",       "19.99"},
            {"Kinetic Sand 1kg",     "Moulds and flows, mess-free play",          "21.99"},
        }},
        new Object[]{"Green Thumb", "Plants, seeds, and garden supplies", "owner09", new String[][]{
            {"Succulent Mix 6-Pack", "Assorted varieties, 7cm pots",              "24.99"},
            {"Potting Mix 10L",      "Premium blend with perlite and worm cast",  "14.99"},
            {"Herb Seed Collection", "Basil, parsley, chives, coriander",          "9.99"},
            {"Terracotta Pots Set",  "3 sizes: 10, 15, 20cm with saucers",        "22.99"},
            {"Pruning Shears",       "Stainless steel, ergonomic grip",           "19.99"},
            {"Watering Can 5L",      "Copper finish, long spout",                 "27.99"},
            {"Liquid Fertiliser 1L", "All-purpose NPK, 10ml per 5L water",       "12.99"},
            {"Garden Gloves",        "Nitrile-coated, touchscreen fingertips",    "11.99"},
            {"Bamboo Plant Stakes",  "Pack of 20, 60cm, natural finish",           "7.99"},
            {"Self-Watering Planter","Reservoir base, oval, 30cm",               "29.99"},
            {"Hanging Macramé Pot",  "Cotton rope, fits 12cm pot",               "18.99"},
            {"Vegetable Seed Kit",   "Tomato, cucumber, lettuce, carrot",        "11.99"},
            {"Soil Moisture Meter",  "3-in-1: moisture, pH, light",              "14.99"},
            {"Trowel & Fork Set",    "Stainless steel, rubber handle",            "16.99"},
            {"Orchid Food Spray",    "Ready-to-use mist, 250ml",                  "9.99"},
        }},
        new Object[]{"Pet Palace", "Everything for your pet", "owner10", new String[][]{
            {"Dry Dog Food 5kg",     "Salmon and sweet potato, grain free",       "54.99"},
            {"Cat Scratching Post",  "Sisal rope, 65cm tall, stable base",        "34.99"},
            {"Interactive Puzzle Feeder","Slow feed bowl, IQ level 2, dogs",      "19.99"},
            {"Cat Litter 10kg",      "Clumping, odour lock, low dust",            "22.99"},
            {"Dog Harness",          "No-pull, reflective, size M",               "29.99"},
            {"Retractable Leash 5m", "Ergonomic handle, wrist strap, 25kg max",  "18.99"},
            {"Stainless Steel Bowl Set","Non-slip, dishwasher safe, 2 x 600ml",  "16.99"},
            {"Catnip Toys 5-Pack",   "Assorted shapes, refillable",              "12.99"},
            {"Orthopedic Dog Bed",   "Memory foam, washable cover, 80x60cm",     "69.99"},
            {"Aquarium Starter Kit", "20L tank, filter, heater, LED light",       "89.99"},
            {"Bird Cage",            "Powder-coated, 2 perches, seed tray",       "54.99"},
            {"Flea & Tick Collar",   "8-month protection, waterproof",            "24.99"},
            {"Grooming Brush",       "Self-cleaning slicker, for long coats",     "17.99"},
            {"Pet Carrier Bag",      "Airline approved, ventilated, 40x25x25cm", "49.99"},
            {"Dental Chews 20-Pack", "Enzymatic, reduces tartar, medium dogs",   "14.99"},
        }},
    };

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userMapper.findUserByName(new User(null, "admin", null, null, null)) != null) {
            System.out.println("Already seeded, skipping.");
            return;
        }
        createUser("admin",     "Admin1234!",    "Admin");
        createUser("customer1", "Customer1234!", "Customer");
        createUser("customer2", "Customer1234!", "Customer");
        createUser("customer3", "Customer1234!", "Customer");

        for (Object[] shopDef : SHOPS) {
            String shopName  = (String) shopDef[0];
            String shopDesc  = (String) shopDef[1];
            String ownerName = (String) shopDef[2];
            String[][] products = (String[][]) shopDef[3];

            User owner = new User(null, ownerName, encoder.encode("Owner1234!"), "ShopOwner", LocalDateTime.now());
            userMapper.add(owner);

            String shopImage = uploadPlaceholder(800, 600);
            Shop shop = new Shop(null, owner.getId(), shopName, LocalDateTime.now(), shopDesc, shopImage);
            shopMapper.add(shop);

            for (String[] p : products) {
                String productImage = uploadPlaceholder(400, 400);
                productMapper.add(new Product(
                        null, shop.getId(), p[0], p[1],
                        new BigDecimal(p[2]), productImage, LocalDateTime.now()));
            }
            System.out.println("Seeded shop: " + shopName);
        }

        System.out.println("Seed complete: 10 shops, 150 products.");
    }

    private void createUser(String username, String password, String role) {
        userMapper.add(new User(null, username, encoder.encode(password), role, LocalDateTime.now()));
    }

    private String uploadPlaceholder(int width, int height) throws IOException {
        byte[] data = downloadImage("https://picsum.photos/" + width + "/" + height);
        return s3Service.uploadImageBytes(data, ".jpg");
    }

    private byte[] downloadImage(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (InputStream in = conn.getInputStream()) {
            return in.readAllBytes();
        }
    }
}
