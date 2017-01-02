function PersoSpider(perso) {
	this.attackAngle = 0;
	this.opposite = false;
	this.attackSpeed = 0;
	this.pathFinder = perso.pathFinder;
	this.perso = perso;
}

PersoSpider.prototype.move = function() {
 if (this.pathFinder.target == null) {
 	this.attackSpeed = 0.5 + random()*0.1;
 	if (this.opposite) {
 		this.attackAngle += PI/4;
 		this.attackSpeed += 1;
 		this.opposite = false;
 	} else {
 		this.attackAngle = random()*2*PI;
 		if (lookFor(perso, 4, Info.ZILDO)) {
 			this.attackAngle = radian(x, y, zildo.x, zildo.y);
 			this.attackAngle = += intervalle(PI/8);
 		}
 	}
 } else {
 	reach(this.attackSpeed);
 	this.attackSpeed *= 0.95;
 }
}

function createSpider(perso) {
	var spider = new PersoSpider(perso);
}
