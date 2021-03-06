package io.github.thewear.thewearandroidClientAPP;

import java.util.ArrayList;
import java.util.List;

public class MCCList {

	/**
	 * MCCList is used as resource of the MCC's (Mobile Country Code)
	 * 
	 * Contains an integer array of all mcc's (mccArray)
	 * 
	 * the get() method is used to retrieve those mcc's as list.
	 */

	public final static String[] ccTLDForMcc = { "gr", "nl", "be", "fr", "mc",
			"ad", "es", "hu", "ba", "hr", "rs", "it", "va", "ro", "ch", "cz",
			"sk", "at", "gb", "gb", "dk", "se", "no", "fi", "lt", "lv", "ee",
			"ru", "ua", "by", "md", "pl", "de", "gi", "pt", "lu", "ie", "is",
			"al", "mt", "cy", "ge", "am", "bg", "tr", "fo", "ge", "gl", "sm",
			"si", "mk", "li", "me", "ca", "pm", "us", "us", "us", "us", "pr",
			"mx", "tc", "gp", "bb", "ag", "ky", "vg", "bm", "gd", "ms", "kn",
			"lc", "vc", "an", "aw", "bs", "ai", "dm", "cu", "do", "ht", "tt",
			"tc", "az", "kz", "bt", "in", "in", "pk", "af", "lk", "mm", "lb",
			"jo", "sy", "iq", "kw", "sa", "ye", "om", "ae", "il", "bh", "qa",
			"mn", "np", "ir", "uz", "tj", "kg", "tm", "jp", "kr", "vn", "hk",
			"mo", "kh", "la", "cn", "tw", "kp", "bd", "mv", "my", "au", "id",
			"tl", "ph", "th", "sg", "bn", "nz", "nr", "pg", "to", "sb", "vu",
			"fj", "as", "ki", "nc", "pf", "ck", "ws", "fm", "mh", "pw", "tv",
			"nu", "eg", "dz", "ma", "tn", "ly", "gm", "sn", "mr", "ml", "gn",
			"ci", "bf", "ne", "tg", "bj", "mu", "lr", "sl", "gh", "ng", "td",
			"cf", "cm", "cv", "st", "gq", "ga", "cg", "cd", "ao", "gw", "sc",
			"sd", "rw", "et", "so", "dj", "ke", "tz", "ug", "bi", "mz", "zm",
			"mg", "re", "zw", "na", "mw", "ls", "bw", "sz", "km", "za", "er",
			"ss", "bz", "gt", "sv", "hn", "ni", "cr", "pa", "pe", "ar", "br",
			"cl", "co", "ve", "bo", "gy", "ec", "py", "sr", "uy", "com" };

	private final static int[] mccArray = { 202, 204, 206, 208, 212, 213, 214,
			216, 218, 219, 220, 222, 225, 226, 228, 230, 231, 232, 234, 235,
			238, 240, 242, 244, 246, 247, 248, 250, 255, 257, 259, 260, 262,
			266, 268, 270, 272, 274, 276, 278, 280, 282, 283, 284, 286, 288,
			289, 290, 292, 293, 294, 295, 297, 302, 308, 310, 311, 313, 316,
			330, 334, 338, 340, 342, 344, 346, 348, 350, 352, 354, 356, 358,
			360, 362, 363, 364, 365, 366, 368, 370, 372, 374, 376, 400, 401,
			402, 404, 405, 410, 412, 413, 414, 415, 416, 417, 418, 419, 420,
			421, 422, 424, 425, 426, 427, 428, 429, 432, 434, 436, 437, 438,
			440, 450, 452, 454, 455, 456, 457, 460, 466, 467, 470, 472, 502,
			505, 510, 514, 515, 520, 525, 528, 530, 536, 537, 539, 540, 541,
			542, 544, 545, 546, 547, 548, 549, 550, 551, 552, 553, 555, 602,
			603, 604, 605, 606, 607, 608, 609, 610, 611, 612, 613, 614, 615,
			616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 626, 627, 628,
			629, 630, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640, 641,
			642, 643, 645, 646, 647, 648, 649, 650, 651, 652, 653, 654, 655,
			657, 659, 702, 704, 706, 708, 710, 712, 714, 716, 722, 724, 730,
			732, 734, 736, 738, 740, 744, 746, 748, 901 };

	/**
	 * get() returns the mcc's as List<Integer>
	 */

	public static List<Integer> get() {
		final List<Integer> mccList = new ArrayList<Integer>();
		// Add all mcc codes to the mccList
		for (int n = 0; n <= mccArray.length - 1; n++) {
			mccList.add(mccArray[n]);
		}

		return mccList;
	}
}