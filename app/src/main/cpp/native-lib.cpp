#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include "PolygonChecker.h"

static std::vector<std::vector<Point>> polygonEdges{};
static std::vector<Point> onePolygonEdges{};

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_mapswithpolygons_GoogleMapImpl_isPointInsidePolygonCheck(JNIEnv *, jobject,
                                        jdouble point_lat,
                                        jdouble point_lon) {
    std::vector<bool> isInsideCheckList{};
    isInsideCheckList.reserve(polygonEdges.size());
    PolygonChecker polygonChecker{};
    for(const auto& polygon : polygonEdges) {
        isInsideCheckList.emplace_back(
                polygonChecker.is_point_in_polygon(polygon, {point_lat, point_lon}));
    }
    return std::any_of(isInsideCheckList.begin(),isInsideCheckList.end(),
                [](bool isInside){ return isInside; });
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_mapswithpolygons_GoogleMapImpl_loadPolygonEdges(JNIEnv *, jobject, jdouble polygon_lat, jdouble polygon_lon) {
    onePolygonEdges.emplace_back(polygon_lat, polygon_lon);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_mapswithpolygons_GoogleMapImpl_loadNativeGlobalPoylgons(JNIEnv *, jobject) {
    polygonEdges.emplace_back(onePolygonEdges);
    onePolygonEdges.clear();
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_mapswithpolygons_GoogleMapImpl_polygonsIntersectionCheck(JNIEnv *env,
                                                                          jobject thiz) {
    if(polygonEdges.size() < 2) return false;
    PolygonChecker polygonChecker{};
    for(size_t i{0}; i < polygonEdges.size() - 1; ++i) {
        for (size_t j = i+1; j < polygonEdges.size(); ++j) {
            if (polygonChecker.polygonsIntersect(polygonEdges[i], polygonEdges[j])) {
                return true;
            }
        }
    }
    return false;
}


extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_mapswithpolygons_GoogleMapImpl_calculatePolygonAreaNative(JNIEnv *env, jobject thiz) {
    PolygonChecker polygonChecker{};
    double result = polygonChecker.polygonArea(onePolygonEdges);
    onePolygonEdges.clear();
    return result;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_mapswithpolygons_GoogleMapImpl_clearNativePolygons(JNIEnv *env, jobject thiz) {
    polygonEdges.clear();
}