//
// Created by Emre Aytac on 22.11.2024.
//

#include "PolygonChecker.h"

bool PolygonChecker::is_point_in_polygon(const std::vector<Point> &polygon, const Point &point) {
    // Number of vertices in the polygon
    size_t n = polygon.size();
    // Count of intersections
    int count = 0;

    // Iterate through each edge of the polygon
    for (int i = 0; i < n; i++) {
        Point p1 = polygon[i];
        // Ensure the last point connects to the first point
        Point p2 = polygon[(i + 1) % n];

        // Check if the point's y-coordinate is within the
        // edge's y-range and if the point is to the left of
        // the edge
        if ((point.y > std::min(p1.y, p2.y))
            && (point.y <= std::max(p1.y, p2.y))
            && (point.x <= std::max(p1.x, p2.x))) {
            // Calculate the x-coordinate of the
            // intersection of the edge with a horizontal
            // line through the point
            double xIntersect = (point.y - p1.y)
                                * (p2.x - p1.x)
                                / (p2.y - p1.y)
                                + p1.x;
            // If the edge is vertical or the point's
            // x-coordinate is less than or equal to the
            // intersection x-coordinate, increment count
            if (p1.x == p2.x || point.x <= xIntersect) {
                count++;
            }
        }
    }
    // If the number of intersections is odd, the point is
    // inside the polygon
    return count % 2 == 1;
}

double PolygonChecker::crossProduct(const Point &O, const Point &A, const Point &B) {
    return (A.x - O.x) * (B.y - O.y) - (A.y - O.y) * (B.x - O.x);
}

bool PolygonChecker::onSegment(const Point &a, const Point &b, const Point &p) {
    return p.x <= std::max(a.x, b.x) && p.x >= std::min(a.x, b.x) &&
           p.y <= std::max(a.y, b.y) && p.y >= std::min(a.y, b.y);
}

bool PolygonChecker::doIntersect(const Point &A, const Point &B, const Point &C, const Point &D) {
    // General case: Check if the segments straddle each other
    double cross1 = crossProduct(A, B, C);
    double cross2 = crossProduct(A, B, D);
    double cross3 = crossProduct(C, D, A);
    double cross4 = crossProduct(C, D, B);

    // Check if the segments straddle each other
    if (cross1 * cross2 < 0 && cross3 * cross4 < 0) {
        return true;
    }

    // Special cases: Check if the points are collinear and on the segment
    if (cross1 == 0 && onSegment(A, B, C)) return true;
    if (cross2 == 0 && onSegment(A, B, D)) return true;
    if (cross3 == 0 && onSegment(C, D, A)) return true;
    if (cross4 == 0 && onSegment(C, D, B)) return true;

    return false;
}

bool PolygonChecker::polygonsIntersect(const std::vector<Point> &poly1,
                                       const std::vector<Point> &poly2) {
    size_t n1 = poly1.size();
    size_t n2 = poly2.size();

    // Check each edge of poly1 with each edge of poly2
    for (int i = 0; i < n1; i++) {
        for (int j = 0; j < n2; j++) {
            // Get the edges of the two polygons
            Point A = poly1[i];
            Point B = poly1[(i + 1) % n1]; // Wrap around to first point
            Point C = poly2[j];
            Point D = poly2[(j + 1) % n2]; // Wrap around to first point

            // Check if the edges intersect
            if (doIntersect(A, B, C, D)) {
                return true;  // If any edges intersect, return true
            }
        }
    }

    return false;  // No intersections found
}


double PolygonChecker::polygonArea(const std::vector<Point>& points) {
    // Initialize area
    double area = 0.0;

    // Calculate value of shoelace formula
    size_t j = points.size() - 1;
    for (size_t i = 0; i < points.size(); i++)
    {
        area += (points[j].x + points[i].x) * (points[j].y - points[i].y);
        j = i;  // j is previous vertex to i
    }

    // Return absolute value
    return std::abs(area / 2.0);
}


