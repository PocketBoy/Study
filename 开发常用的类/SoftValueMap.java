package cn.ithm.fragmentdemohm23.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * ���Ͷ�������ü���������
 * 
 * @author Administrator
 * 
 * @param <K>
 * @param <V>
 */
public class SoftValueMap<K, V> extends HashMap<K, V> {
	// ����V�����ü���������
	private HashMap<K, SoftValue<K, V>> temp;

	private ReferenceQueue<V> queue;// װ�ƴ��ӣ������ֻ�V��

	public SoftValueMap() {
		// ������
		// Object v=new Object();//ռ���ڴ��
		// SoftReference sr=new SoftReference(v);// ������v�����ý��

		// �ٽ��ֻ���ռ���ڴ�϶�Ķ�����ӵ����ӣ�SoftReference������
		// ��һ���ֻ���͵�ˣ����ƴ��ӻ���

		temp = new HashMap<K, SoftValue<K, V>>();
		queue = new ReferenceQueue<V>();
	}

	@Override
	public V put(K key, V value) {
		// SoftValue<K,V> sr = new SoftValue<K,V>(value);
		// ��GC�ڻ����ֻ�ʱ�򣬻Ὣsr��ӵ�queue
		SoftValue<K, V> sr = new SoftValue<K, V>(value, key, queue);// ��sr��queue��
		temp.put(key, sr);
		return null;
	}

	@Override
	public V get(Object key) {
		clearSR();
		SoftValue<K, V> sr = temp.get(key);
		if (sr != null) {
			// ��������ö����Ѿ��ɳ���������������������˷��������� null��
			return sr.get();
		} else {
			return null;
		}
	}

	@Override
	public boolean containsKey(Object key) {
		clearSR();
		// ʲô�Žк���
		// ��ȡ�����ӣ��Ӵ��������õ��ֻ��ˣ�����
		SoftValue<K, V> sr = temp.get(key);
		// temp.containsKey(key);

		/*
		 * if(sr.get()!=null) { return true; }else{ return false; }
		 */
		if (sr != null) {
			return sr.get() != null;
		}
		return false;

	}

	/**
	 * �������յ��ֻ����ƴ���
	 */
	private void clearSR() {
		// ����һ��ѭ��һ��temp����������յ��ˣ������������
		// �ڴ�����ڴ滹�ܹ����㣺temp������Ԫ�ر�����

		// �����������GC���ֻ����գ����ƴ��ӵ����ü�¼��һ�����Լ�������������
		// ���Լ�����������

		// �������һ���������õĶ�����Ӹö�����"�Ƴ�"�˶��󲢷��ء�����˷����������� null��
		SoftValue<K, V> poll = (SoftValue<K, V>) queue.poll();
		while (poll != null) {
			// ��temp��srǿ���ö������
			temp.remove(poll.key);
			poll = (SoftValue<K, V>) queue.poll();
		}
	}

	@Override
	public void clear() {
		temp.clear();
	}

	/**
	 * ��ǿ��Ĵ��ӣ��洢��һ��key��Ϣ
	 * 
	 * @author Administrator
	 * 
	 * @param <K>
	 * @param <V>
	 */
	private class SoftValue<K, V> extends SoftReference<V> {
		private Object key;

		public SoftValue(V r, Object key, ReferenceQueue<? super V> q) {
			super(r, q);
			this.key = key;
		}
	}

}
