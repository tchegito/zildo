package zildo.monde.sprites.desc;

import java.util.ArrayList;
import java.util.List;

public class ZSpriteLibrary {

	final static List<SpriteDescription> list = new ArrayList<SpriteDescription>();
	
	public final static List<SpriteDescription> getList() {
		return list;
	}
	
	static {
		// Select only posable elements (discard all in-game animations)
		for (ElementDescription desc : ElementDescription.values()) {
			switch (desc) {
			case SHADOW:
			case SHADOW_LARGE:
			case SHADOW_MINUS:
			case SHADOW_SMALL:
			case SMOKE_SMALL:
			case NETTLE_LEAF:
			case DEATH_ANIM1:
			case DEATH_ANIM2:
			case DEATH_ANIM3:
			case DEATH_ANIM4:
			case DEATH_ANIM5:
			case DEATH_ANIM6:
			case DEATH_ANIM7:
			case HEART:
			case DROP_SMALL:
			case DROP_MEDIUM:
			case POISONBALL:
			case POISONGOOP:
			case SPARK_UPLEFT:
			case SPARK_UPRIGHT:
			case GOLDCOIN2:
			case GOLDCOIN3:
			case THREEGOLDCOINS2:
			case THREEGOLDCOINS3:
			case GOLDPURSE2:
			case GOLDPURSE3:
			case SMOKE_FEET1:
			case SMOKE_FEET2:
			case SMOKE_FEET3:
			case ARROW_DOWN:
			case ARROW_LAND_DOWN1:
			case ARROW_LAND_DOWN2:
			case ARROW_LAND_DOWN3:
			case ARROW_LAND_LEFT1:
			case ARROW_LAND_LEFT2:
			case ARROW_LAND_LEFT3:
			case ARROW_LAND_RIGHT1:
			case ARROW_LAND_RIGHT2:
			case ARROW_LAND_RIGHT3:
			case ARROW_LAND_UP1:
			case ARROW_LAND_UP2:
			case ARROW_LAND_UP3:
			case ARROW_LEFT:
			case ARROW_RIGHT:
			case BOOMERANG2:
			case BOOMERANG3:
			case BOOMERANG4:
			case ENEMYARC_DOWN1:
			case ENEMYARC_DOWN2:
			case ENEMYARC_LEFT1:
			case ENEMYARC_LEFT2:
			case ENEMYARC_RIGHT2:
			case ENEMYARC_UP1:
			case ENEMYARC_UP2:
			case IMPACT1:
			case IMPACT2:
			case IMPACT3:
			case IMPACT4:
			case REDBALL1:
			case REDBALL2:
			case REDBALL3:
			case EXPLO1:
			case EXPLO2:
			case EXPLO3:
			case EXPLOSMOKE1:
			case EXPLOSMOKE2:
			case EXPLOSMOKE3:
			case FIRE_SPIRIT1:	case FIRE_SPIRIT2: case FIRE_SPIRIT3: case QUAD5:
			case QUAD6:
			case NOTE: case NOTE2:
			case WATER_ANIM1: case WATER_ANIM2: case WATER_ANIM3: case WATER_ANIM4:
			case LAVADROP1: case LAVADROP2: case LAVADROP3: case LAVADROP4:
			case SEWER_SMOKE1: case SEWER_SMOKE2:
			case SEWER_VOLUT1: case SEWER_VOLUT2: case SEWER_VOLUT3: case SEWER_VOLUT4:
			case WATERWAVE1: case WATERWAVE2: case WATERWAVE3:
			case REDSPHERE1: case REDSPHERE2: case REDSPHERE3:
			case BROWNSPHERE1: case BROWNSPHERE2: case BROWNSPHERE3:
			case CANDLE2: case CANDLE3:
			case CAULDRON2: case CAULDRON3:
			//case LAUNCHER2:
			case BULLET:
			case HEARTH2: case HEARTH3: case HEARTH4: case HEARTH5: case HEARTH6:
			case FIREWIND1: case FIREWIND2: case FIREWIND3:
			case GNAP1: case GNAP2: case GNAP3: case GNAP4: case GNAP5:
			case SAND1: case SAND2: case SAND3: case SAND4: case SAND5:
			case STRAWF1: case STRAWF2: case STRAWF3:
				break;
			default:
				list.add(desc);
			}
		}
		for (GearDescription descGear : GearDescription.values()) {
			switch (descGear) {		
			case GREEN_DOOR_OPENING:
			case CAVE_KEYDOOR_OPENING:
			case HIDDENDOOR_OPENING:
				break;
			default:
				list.add(descGear);
			}
		}
	}
}
