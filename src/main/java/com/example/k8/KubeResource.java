package com.example.k8;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class KubeResource extends TimerTask {

	private static Logger LOG = LoggerFactory.getLogger(KubeResource.class);

	static {
		Timer t=new Timer();
		t.scheduleAtFixedRate(new KubeResource(), 0,1000);
	}

	public static void listNodes () throws ApiException, IOException {
		ApiClient client  = Config.defaultClient();
		CoreV1Api api = new CoreV1Api(client);
		V1NodeList nodeList = api.listNode(null, null, null, null, null, null, null, null, 10, false);
		nodeList.getItems()
				.stream()
				.forEach((node) -> LOG.info(String.valueOf(node.getMetadata())));
	}

	public static void listPods() throws IOException, ApiException {
		ApiClient client  = Config.defaultClient();
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> LOG.info(message));
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient newClient = client.getHttpClient()
				.newBuilder()
				.addInterceptor(interceptor)
				.readTimeout(0, TimeUnit.SECONDS)
				.build();

		client.setHttpClient(newClient);
		CoreV1Api api = new CoreV1Api(client);
		String namespace = "ns1";
		V1PodList items = api.listNamespacedPod(namespace,null, null, null, null, null, null, null, null, 10, false);
		items.getItems()
				.stream()
				.map((pod) -> pod.getMetadata().getName() )
				.forEach((name) -> System.out.println("name=" + name));


	}

	@Override
	public void run() {
		try {
			listNodes();
			listPods();
		} catch (ApiException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

