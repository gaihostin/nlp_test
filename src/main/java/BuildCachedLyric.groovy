@Grab('redis.clients:jedis:2.7.3')
@Grab('com.taobao.tddl:tddl-client:5.1.22-1')
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Pipeline

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.regex.Pattern

/**
 * date 16/6/6 11:48
 *
 * @author: shuhan.lyn@alibaba-inc.com
 */


public class FillRedisCacheLyric implements Callable<Long> {

    Map<String, String> BatchLyrics;
    Jedis oneJedis;
    String rds_key;
    Pattern patt = Pattern.compile("[\\[<{（(][\\s\\S]*?[)）}>\\]]", Pattern.CASE_INSENSITIVE);

    public FillRedisCacheLyric(Map<String, String> BatchLyrics, Jedis jedis, String rds_key) {
        this.BatchLyrics = BatchLyrics;
        this.oneJedis = jedis;
        this.rds_key = rds_key
    }

    @Override
    public synchronized Long call() {

        Long count = 0L;
        try {

            Pipeline p = oneJedis.pipelined()

            for (String song_id : BatchLyrics.keySet()) {
                String lyric_path = BatchLyrics.get(song_id)
                String lyric_url = "http://img.xiami.net/" + lyric_path
                String text = new URL(lyric_url).text
                if (text == null) {
                    continue
                }
                String response = patt.matcher(text).replaceAll("")
                response = response.replaceAll("[\\s]+", " ")
                if (response != null && response.length() != 0) {
                    println song_id
                    p.hset(rds_key + "_tmp", song_id, response.trim())
                    count++
                }
            }

            p.sync()

            System.out.println ("Prc " + count)

        } catch (Exception e) {
            println e.getStackTrace()
        }
        oneJedis.close();

        return count;
    }


}


JedisPoolConfig config = new JedisPoolConfig();

config.setMaxTotal(60);
config.setMaxIdle(2);
config.setMaxWaitMillis(600000);

def JedisPool jedisPool = new JedisPool(config, "45630c92-49a5-49.m.cnsh2.kvstore.aliyuncs.com", 6379);

def final String rds_key = "LyricCache"

def int threadCount = 10;

long start = System.currentTimeMillis()


Map<String, String> songLyricMap = new HashMap<>()

new File("/home/admin/redis-data-cache/dict/song_lyrics").eachLine { line ->
    String[] items = "${line}".split('\t')
    if (items.length == 2) {
        songLyricMap.put(items[0], items[1])
     }
 }

if (songLyricMap.size() <  threadCount) {
    threadCount = songLyricMap.size();
 }

int step = songLyricMap.size()/threadCount

System.out.println("the step is " + step)

ArrayList<Callable<Long> > callers = new ArrayList<>();

Map<String, String> BatchLyrics = new HashMap<>();
ExecutorService pool = null;

def Jedis oneJedis = null
 try {

     for (String id : songLyricMap.keySet()) {
         if (BatchLyrics.size() != step) {
             BatchLyrics.put(id, songLyricMap.get(id))
         } else {
             oneJedis = jedisPool.getResource()
             oneJedis.auth("45630c92-49a5-49:MhxzKhl2007")
             callers.add(new FillRedisCacheLyric(BatchLyrics, oneJedis, rds_key));
             BatchLyrics = new HashMap<>()
             BatchLyrics.put(id, songLyricMap.get(id))
         }
     }

     if (BatchLyrics.size() > 0) {
         oneJedis = jedisPool.getResource()
         oneJedis.auth("45630c92-49a5-49:MhxzKhl2007")
         callers.add(new FillRedisCacheLyric(BatchLyrics, oneJedis, rds_key));
     }

     System.out.println(callers.size())

     pool = Executors.newFixedThreadPool(callers.size());


     Long downloadNum = 0L;
     List<Future<Long>> LyricsNum = pool.invokeAll(callers);

     for (Future<Long> num : LyricsNum) {
         downloadNum += num.get()
     }

     System.out.println("the "+ threadCount + " batch " +  downloadNum + "  cost " + (System.currentTimeMillis() - start))

 } catch (Exception e) {
     println e.getStackTrace()
 } finally {
     pool.shutdown()
 }


try {
    oneJedis = jedisPool.getResource()
    oneJedis.auth("45630c92-49a5-49:MhxzKhl2007")

    oneJedis.rename(rds_key + "_tmp", rds_key)
} catch (Exception) {

} finally {
    oneJedis.close()
}








