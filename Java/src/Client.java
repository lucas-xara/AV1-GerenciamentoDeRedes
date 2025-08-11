import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        System.out.println("Connected to the server!");

        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        int hp = 3;
        int ammo = 0;

        // Antes do loop principal

        System.out.print("Enter your name: ");
        String playerName = teclado.readLine();

// Envia o nome para o servidor
        saida.println(playerName);

// Recebe o nome do servidor
        String serverName = entrada.readLine();


        while (true) {
            clearScreen();

            // 1) Player chooses action
            String playerAction = chooseAction(teclado, hp, ammo);

            // 2) Send to server
            saida.println(playerAction);
            System.out.println("Waiting for other player's move...");

            // 3) Receive action and status from server
            String enemyAction = entrada.readLine();
            if (enemyAction == null) break;

            int enemyHp = Integer.parseInt(entrada.readLine());
            int enemyAmmo = Integer.parseInt(entrada.readLine());

            // 4) Calculate result
            int[] status = calculateResult(playerAction, enemyAction, hp, ammo);
            hp = status[0];
            ammo = status[1];

            // 5) Show result
            System.out.println(playerName + " chose: " + playerAction);
            System.out.println(serverName + " chose: " + enemyAction);
            System.out.println("Your HP:      " + getHpBar(hp) + "  |  Ammo: " + getAmmoBar(ammo));
            System.out.println(serverName + " HP:     " + getHpBar(enemyHp) + "  |  Ammo: " + getAmmoBar(enemyAmmo));
            System.out.println("--------------------------------------------------");

            // 6) End game if player is dead
            if (hp <= 0) {
                System.out.println("You lost!");
                break;
            }

            if (enemyHp <= 0) {
                System.out.println("You lucwon!");
                break;
            }

            System.out.println("Press Enter to continue...");
            teclado.readLine();
        }

        socket.close();
    }

    public static String chooseAction(BufferedReader teclado, int hp, int ammo) throws IOException {
        while (true) {
            clearScreen();

            System.out.println("################# CHOOSE YOUR ACTION #################");
            System.out.println("##                                                  ##");
            System.out.println("##    1 - RELOAD      2 - DEFEND     3 - SHOOT      ##");
            System.out.println("##                                                  ##");
            System.out.println("######################################################");
            System.out.println("HP: " + getHpBar(hp) + "  |  Ammo: " + getAmmoBar(ammo));
            System.out.print("Your turn! ");
            String input = teclado.readLine();

            if (input.equals("1") || input.equalsIgnoreCase("reload")) return "reload";
            if (input.equals("2") || input.equalsIgnoreCase("defend")) return "defend";
            if (input.equals("3") || input.equalsIgnoreCase("shoot")) return "shoot";

            System.out.println("Invalid option! Press Enter and try again.");
            teclado.readLine();
        }
    }

    public static int[] calculateResult(String playerAction, String enemyAction, int currentHp, int currentAmmo) {
        int hp = currentHp;
        int ammo = currentAmmo;

        // Update ammo
        if (playerAction.equals("reload")) {
            if (ammo < 1) ammo++;
        } else if (playerAction.equals("shoot")) {
            if (ammo > 0) ammo--;
        }

        // Check if enemy shot and player defended
        boolean enemyShot = enemyAction.equals("shoot");
        boolean playerDefended = playerAction.equals("defend");

        if (enemyShot && !playerDefended) {
            hp--;
        }

        return new int[]{hp, ammo};
    }

    public static String getAmmoBar(int ammo) {
        return (ammo == 1) ? "▮" : "▯";
    }

    public static String getHpBar(int hp) {
        switch (hp) {
            case 3: return "▮▮▮";
            case 2: return "▮▮▯";
            case 1: return "▮▯▯";
            default: return "▯▯▯";
        }
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
