package no.nav.poao_gcp_proxy.utils

object UrlUtils {
	/**
	 * Removes the start path from a request.
	 * Ex: startPath = /proxy, requestPath=/proxy/some/path --> /some/path
	 * @param startPath path that will be stripped
	 * @param requestPath path that will be stripped from
	 * @return the requestPath without the startPath
	 */
	fun stripStartPath(startPath: String, requestPath: String): String {
		return if (requestPath.startsWith(startPath)) {
			requestPath.substring(startPath.length)
		} else requestPath
	}

	/**
	 * Returns the first segment of a url path.
	 * /some-path/test -> some-path
	 * some-path/test -> some-path
	 * @param urlPath the path to get the segment from
	 * @return the first segment of the path
	 */
	fun getFirstSegment(urlPath: String): String {
		return if (urlPath.startsWith("/")) {
			urlPath.substring(1, indexOfOrEnd(urlPath, "/", 1))
		} else urlPath.substring(0, indexOfOrEnd(urlPath, "/", 0))
	}

	private fun indexOfOrEnd(str: String, searchStr: String, fromIndex: Int): Int {
		val indexOf = str.indexOf(searchStr, fromIndex)
		return if (indexOf >= 0) indexOf else str.length
	}
}