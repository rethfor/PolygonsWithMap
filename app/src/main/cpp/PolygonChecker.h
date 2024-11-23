//
// Created by Emre Aytac on 22.11.2024.
//

#ifndef MAPSWITHPOLYGONS_POLYGONCHECKER_H
#define MAPSWITHPOLYGONS_POLYGONCHECKER_H

#include <vector>
#include <algorithm>

struct Point {
    // Coordinates of the point
    Point(double x_in, double y_in) : x{x_in}, y{y_in} {}
    double x{}, y{};
};

class PolygonChecker {
private:
    PolygonChecker() = default;
    /// Helper function to compute the cross product of vectors OA and OB
    /// A positive cross product indicates a counter-clockwise turn, negative indicates clockwise, and zero indicates collinear points
    static double crossProduct(const Point& O, const Point& A, const Point& B);
    /// Helper function to check if point p is on segment ab
    static bool onSegment(const Point& a, const Point& b, const Point& p);
    /// Function to check if two segments AB and CD intersect
    static bool doIntersect(const Point& A, const Point& B, const Point& C, const Point& D);
public:
    bool is_point_in_polygon(const std::vector<Point>& polygon, const Point& point);
    // Function to check if two polygons intersect
    bool polygonsIntersect(const std::vector<Point>& poly1, const std::vector<Point>& poly2);
    double polygonArea(const std::vector<Point>& points);
};


#endif //MAPSWITHPOLYGONS_POLYGONCHECKER_H
