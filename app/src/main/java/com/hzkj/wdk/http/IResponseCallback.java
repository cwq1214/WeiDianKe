package com.hzkj.wdk.http;

import com.hzkj.wdk.model.ErrorModel;

public abstract class IResponseCallback<T> {

	public abstract void onSuccess(T t);

	public abstract void onFailure(ErrorModel errorModel);

	public abstract void onStart();

	public void onLoading(long total, long current, boolean isUploading) {
	}

}
