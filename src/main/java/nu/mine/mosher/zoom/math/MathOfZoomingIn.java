/*
 *     Copyright © 2026, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nu.mine.mosher.zoom.math;

/*

https://www.gksander.com/posts/math-of-zooming-in/

The Linear Algebra approach
if you want to scale by s
about the point (x0,y0)
but scaling happens about the origin (0,0)
then you can think of this as applying the following transformations:
    First, scale the space by s.
    Then, translate x by x0 * (1−s) and y by y0 * (1−s).



Window Mapping
On mousewheel, determine what percentage the pointer is from the x-minimum bound of the input window. Call this percentage X%
Assume you want to grow/shrink the input window width by dW units.
Then remove X%  * dW  from the minimum x-bound for the input window
and add  (1−X%) * dW  to   the maximum x-bound for the input window.
Do the same thing for the y-bounds.

  const hitPercentX = (x - inputWindow.xMin) / (inputWindow.xMax - inputWindow.xMin);
  const hitPercentY = (y - inputWindow.yMin) / (inputWindow.yMax - inputWindow.yMin);

  inputWindow.xMin -=      hitPercentX  * deltaW;
  inputWindow.xMax += (1 - hitPercentX) * deltaW;
  inputWindow.yMin -=      hitPercentY  * deltaH;
  inputWindow.yMax += (1 - hitPercentY) * deltaH;

 */
public class MathOfZoomingIn {
    /*
        // linearly map a value x from [xi, xf] into [yi, yf]
        const linearMap = (
          x: number,
          xi: number,
          xf: number,
          yi: number,
          yf: number,
        ) => {
          return yi + ((yf - yi) / (xf - xi)) * (x - xi);
        };
     */

    /*
        type Point = { x: number; y: number };

        type ViewWindow = {
          xMin: number;
          xMax: number;
          yMin: number;
          yMax: number;
        };

        // Maps one 2D space to another such that inputWindow maps into outputWindow
        const mapPoint = (
            point: Point,
            inputWindow: ViewWindow,
            outputWindow: ViewWindow,
                    ) => {
                return {
                    // map in the x-direction
                    x: linearMap(
                        point.x,
                        inputWindow.xMin,
                        inputWindow.xMax,
                        outputWindow.xMin,
                        outputWindow.xMax,
                    ),
                    // map in the y-direction
                    y: linearMap(
                        point.y,
                        inputWindow.yMin,
                        inputWindow.yMax,
                        outputWindow.yMin,
                        outputWindow.yMax,
                    ),
                 };
            };
        */





/*
canvas.addEventListener("wheel", (e) => {
  e.preventDefault();

  // _Input_ coordinates where mousewheel occurred
  const { x, y } = mapPoint(
    { x: e.offsetX, y: e.offsetY },
    outputWindow,
    inputWindow,
  );

  // What percentage away from the bottom-left of the input window
  //  was the wheel event registered at?
  const hitPercentX =
    (x - inputWindow.xMin) / (inputWindow.xMax - inputWindow.xMin);
  const hitPercentY =
    (y - inputWindow.yMin) / (inputWindow.yMax - inputWindow.yMin);

  // How much wider/taller we should make the input window.
  const deltaW = e.deltaY / 20;
  const deltaH = e.deltaY / 20;

  // Expand x p_x% to left and (1 - p_x)% to the right,
  // Expand y p_y% down and (1 - p_y)% up.
  inputWindow.xMin -= hitPercentX * deltaW;
  inputWindow.xMax += (1 - hitPercentX) * deltaW;
  inputWindow.yMin -= hitPercentY * deltaH;
  inputWindow.yMax += (1 - hitPercentY) * deltaH;

  // Trigger re-draw with new input window.
  draw();
});




const SIZE = 300;

// Get your canvas ref
const canvas = document.querySelector("canvas");
const ctx = canvas.getContext("2d");

// We'll define our input and output windows
const inputWindow: ViewWindow = {
  xMin: -18,
  xMax: 18,
  yMin: -18,
  yMax: 18,
};
 // Notice how the y's are flipped! This is because Cartesian/canvas
 //  differences, where we want to map e.g. -18 in Cartesian
 //  to the bottom of the canvas window, etc.
const outputWindow: ViewWindow = {
        xMin: 0,
                xMax: SIZE,
                yMin: SIZE,
                yMax: 0,
    };

// Our drawing function
const draw = () => {
        ctx.clearRect(0, 0, SIZE, SIZE);

        ctx.fillStyle = "red";
        ctx.lineWidth = 5;

        // Drawing logic for the heart.
        ctx.beginPath();
        let pt = mapPoint(heart(0), inputWindow, outputWindow);
        ctx.moveTo(pt.x, pt.y);
        for (let t = 0; t < 2 * Math.PI; t += 0.01) {
            // Map heart point from Cartesian to our canvas
            pt = mapPoint(heart(t), inputWindow, outputWindow);
            ctx.lineTo(pt.x, pt.y);
        }
        ctx.stroke();
        ctx.fill();
    };

    draw();

    */
}
