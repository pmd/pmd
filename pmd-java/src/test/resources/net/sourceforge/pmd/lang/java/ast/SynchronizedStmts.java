
class Sync {
    public static void getInstance() {
        synchronized (0) { // note that synchronized is also a modifier
           return;
        }
    }

}
