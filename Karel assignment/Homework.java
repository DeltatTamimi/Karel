import stanford.karel.SuperKarel;

public class Homework extends SuperKarel {
    private static int beepers = 0;

    public void run() {
        int height = 1;
        int width = 1;
        setBeepersInBag(1000);
        beepers = 0;

        while (frontIsClear()) {
            move();
            width++;
        }
        turnLeft();
        while (frontIsClear()) {
            move();
            height++;
        }
        turnLeft();
        System.out.println("Measuring steps = " + (width + height - 2));
        int steps = 0;
        if (height == 1 || width == 1) {
            System.out.println("Number of steps = " + handleOneDimension(height, width) + '\n' + "Number of beepers = " + beepers);
        } else if (height == 2 || width == 2) {
            System.out.println("Number of steps = " + handleTwoDimension(height, width) + '\n' + "Number of beepers = " + beepers);
        } else if (height % 2 != 0 && width % 2 != 0) {
            System.out.println("Number of steps = " + handleOddDimensions(height, width) + '\n' + "Number of beepers = " + beepers);
        } else if (height % 2 == 0 && width % 2 == 0) {
            System.out.println("Number of steps = " + handleEvenDimensions(height, width) + '\n' + "Number of beepers = " + beepers);
        } else {
            System.out.println("Number of steps = " + handleMixedDimensions(height, width) + '\n' + "Number of beepers = " + beepers);
        }
    }

    private int calculatePlusCount(int height, int width) {
        height--;
        width--;
        return width * 2 + height + height / 2;
    }

    private int calculateSCount(int height, int width) {
        if ((width - 3) % 4 != 0) return 10000;
        return (height - 1) * 3 + width - 1 - (width - 3) / 4;
    }

    private int moveAndCountSteps(int steps, boolean put) {
        int count = 0;
        while (steps > 0) {
            move();
            if (put) putBeeperIfNotPresent();
            steps--;
            count++;
        }
        return count;
    }

    private void putBeeperIfNotPresent() {
        if (!beepersPresent()) {
            putBeeper();
            beepers++;
        }
    }

    private int sShapeMovement(boolean swap, int length, int length2) {
        int steps = 0;
        while ((!swap && notFacingWest()) || (swap && notFacingSouth())) {
            turnLeft();
        }
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(length, true);
        turnDirection(!swap, 2);
        steps += moveAndCountSteps(length2, false);
        turnDirection(!swap, 2);
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(length, true);
        turnDirection(!swap, 1);
        steps += moveAndCountSteps(length2, false);
        turnDirection(!swap, 1);
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(length, true);

        return steps;
    }

    private int executePlusShapeMovement(int height, int width, boolean swap) {
        if (swap) turnLeft();
        int steps = moveAndCountSteps(width /2, false);
        turnDirection(swap, 1);
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(height/2 , true);

        for (int i = 0; i < 3; i++) {
            turnLeft();
            int len = (i % 2 == 0) ? width : height;
            putBeeperIfNotPresent();
            steps += moveAndCountSteps(len / 2, true);
            turnAround();
            if (i != 2) steps += moveAndCountSteps(len / 2, false);
        }
        return steps;
    }

    private int fillRemainingSteps(boolean swap) {
        int steps = 0;
        int count = 0;

        if (frontIsBlocked()) return 0;

        while (frontIsClear()) {
            steps += moveAndCountSteps(1, true);
            count++;
        }

        turnDirection(swap, 2);
        steps += moveAndCountSteps(1, true);
        turnDirection(swap, 2);
        steps += moveAndCountSteps(count - 1, true);

        return steps;
    }

    private void turnDirection(boolean swap, int direction) {
        if (!swap) {
            if (direction == 1) turnLeft();
            else turnRight();
        } else {
            if (direction == 1) turnRight();
            else turnLeft();
        }
    }

    private int handleTwoDimension(int height, int width) {
        int steps = 0;
        boolean swap = false;

        if (height == 2) {
            int temp = width;
            width = height;
            height = temp;
            swap = true;
        }

        if (swap) turnLeft();

        if (height < 7) {
            int pattern = 0;

            for (int i = 0; i < 7 && frontIsClear(); i++) {
                steps += moveAndCountSteps(1, pattern % 2 == 0);
                if (pattern < 2 || pattern > 3) {
                    turnDirection(swap, 1);
                } else {
                    turnDirection(swap, 2);
                }
                pattern++;
            }

            if (pattern != 7) return steps;

            turnAround();
            steps += fillRemainingSteps(swap);
        } else {
            int chamberSize = (height - 3) / 4;
            turnDirection(swap, 1);
            steps += moveAndCountSteps(chamberSize, false);
            steps += sShapeMovement(swap, 1, chamberSize + 1);
            turnDirection(swap, 1);

            if ((height - 3) % 4 == 0) return steps;

            steps += moveAndCountSteps(chamberSize + 1, false);
            putBeeperIfNotPresent();
            turnDirection(swap, 1);
            steps += moveAndCountSteps(1, true);
            turnDirection(swap, 2);
            steps += fillRemainingSteps(swap);
        }

        return steps;
    }

    private int handleOneDimension(int height, int width) {
        int steps = 0;
        boolean swap = false;

        if (height == 1) {
            int temp = width;
            width = height;
            height = temp;
            swap = true;
        }

        if (!swap) turnLeft();

        int chamberSize = height > 8 ? height / 4 - 1 : 1;
        int chambers = 0;

        while (frontIsClear() && height != 2) {
            steps += moveAndCountSteps(chamberSize - (chambers == 0 ? 1 : 0), false);
            chambers++;

            if (frontIsClear()) {
                steps += 1;
                move();
                putBeeperIfNotPresent();
            }

            if (chambers == 4) break;
        }

        while (frontIsClear() && height != 2) {
            steps += moveAndCountSteps(1, true);
        }

        return steps;
    }

    private int handleOddDimensions(int height, int width) {
        int steps = 0;
        int plusCount1 = calculatePlusCount(height, width);
        int plusCount2 = calculatePlusCount(width, height);
        int minimumPlusCount = Math.min(plusCount1, plusCount2);
        int sCount1 = calculateSCount(height, width);
        int sCount2 = calculateSCount(width, height);

        if (height >= 7 && width >= 7 && (height - 3) % 4 == 0 && (width - 3) % 4 == 0) {
            if (sCount1 <= sCount2 && sCount1 < minimumPlusCount) {
                int w = (width - 3) / 4;
                steps += moveAndCountSteps(w, false);
                turnLeft();
                steps += sShapeMovement(true, height - 1, w + 1);
            } else if (sCount2 <= sCount1 && sCount2 < minimumPlusCount) {
                int h = (height - 3) / 4;
                turnLeft();
                steps += moveAndCountSteps(h, false);
                steps += sShapeMovement(false, width - 1, h + 1);
            } else {
                if (plusCount1 == minimumPlusCount) {
                    steps += executePlusShapeMovement(height, width, false);
                } else {
                    steps += executePlusShapeMovement(width, height, true);
                }
            }
        } else if (height >= 7 && (height - 3) % 4 == 0 && sCount2 < minimumPlusCount) {
            int h = (height - 3) / 4;
            turnLeft();
            steps += moveAndCountSteps(h, false);
            steps += sShapeMovement(false, width - 1, h + 1);
        } else if (width >= 7 && (width - 3) % 4 == 0 && sCount1 < minimumPlusCount) {
            int w = (width - 3) / 4;
            steps += moveAndCountSteps(w, false);
            turnLeft();
            steps += sShapeMovement(true, height - 1, w + 1);
        } else {
            if (minimumPlusCount == plusCount1) {
                steps+=executePlusShapeMovement(height, width, false);
            } else {
                steps+=executePlusShapeMovement(width, height, true);
            }
        }

        return steps;
    }

    private int handleEvenDimensions(int height, int width) {
        boolean swap = false;
        if (width > height) {
            int temp = height;
            height = width;
            width = temp;
            swap = true;
        }

        int halfWidth1 = width / 2;
        int halfWidth2 = halfWidth1 - 1;
        int halfHeight1 = height / 2;
        int halfHeight2 = halfHeight1 - 1;

        int area1 = halfHeight1 * halfWidth2;
        int area2 = halfHeight2 * halfWidth1;

        int difference = Math.max(area1, area2) - Math.min(area1, area2);

        int steps = 0;

        if (swap) turnLeft();

        if (difference > 1) {
            steps += moveAndCountSteps(halfWidth1, false);
            turnDirection(swap, 1);
            int middle = difference % 2;
            putBeeperIfNotPresent();
            steps += moveAndCountSteps((difference - middle) / 2 - 1, true);
            steps += moveAndCountSteps(1, false);
            turnDirection(swap, 1);
            steps += moveAndCountSteps(1, false);
            turnDirection(swap, 2);
        } else {
            steps += moveAndCountSteps(halfWidth2, false);
            turnDirection(swap, 1);
        }

        putBeeperIfNotPresent();
        steps += moveAndCountSteps(halfHeight1 - (difference / 2) - 2, true);
        steps += moveAndCountSteps(1, difference % 2 == 0);
        steps += moveAndCountSteps(1, true);
        turnDirection(swap, 2);
        steps += moveAndCountSteps(1, false);
        turnDirection(swap, 1);

        if (difference % 2 == 0) putBeeperIfNotPresent();
        steps += moveAndCountSteps(halfHeight1 - (difference / 2) - 1, true);

        if (difference / 2 > 0) {
            steps += moveAndCountSteps(1, false);
            turnDirection(swap, 1);
            steps += moveAndCountSteps(1, false);
            turnDirection(swap, 2);
            putBeeperIfNotPresent();
            steps += moveAndCountSteps((difference / 2) - 1, true);
        }

        turnDirection(swap, 1);
        int middle = (difference / 2 > 0) ? 1 : 0;
        steps += moveAndCountSteps(halfWidth1 - middle, false);
        turnDirection(swap, 1);
        steps += moveAndCountSteps(halfHeight2, false);
        turnDirection(swap, 1);
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(halfWidth1 - 1, true);
        steps += moveAndCountSteps(1, difference % 2 == 0);
        turnDirection(swap, 2);
        steps += moveAndCountSteps(1, true);
        turnDirection(swap, 1);
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(halfWidth2, true);

        return steps;
    }

    private int handleMixedDimensions(int height, int width) {
        int steps = 0;
        boolean swap = false;

        if (height % 2 != 0) {
            int temp = height;
            height = width;
            width = temp;
            swap = true;
        }

        int corner = height / 2 * width / 2 - height / 2 * (width / 2 - 1);
        corner = corner % 2;

        if (swap) turnLeft();
        steps += moveAndCountSteps(width / 2, false);
        turnDirection(swap, 1);
        putBeeperIfNotPresent();
        steps += moveAndCountSteps(height - 1, true);
        turnAround();
        steps += moveAndCountSteps(height / 2 - 1, false);
        turnDirection(swap, 2);

        for (int i = 0; i < 2; i++) {
            steps += moveAndCountSteps(width / 4, true);
            steps += moveAndCountSteps(1, (width / 2) % 2 == 1);
            turnDirection(swap, 1);
            steps += moveAndCountSteps(1, true);
            turnDirection(swap, 2);
            steps += moveAndCountSteps(width / 4 - ((width / 2) % 2 == 1 ? 0 : 1), true);

            if (i == 0) {
                turnAround();
                steps += moveAndCountSteps(width / 2, false);
            }
        }

        return steps;
    }


}
