export class MapUtils {

  static singletonMap<K, V>(key: K, value: V): Map<K, V> {
    const map: Map<K, V> = new Map<K, V>();
    map.set(key, value);
    return map;
  }
}
